import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * Simulating memory (Stack and Heap) for our variables.
 * There are 3 "regions" of memory: Global, Local, and Heap.
 * Stack<HashMap<String, Value>> local will represent the local scopes.
 * HashMap<String, Value> global will represent the global scopes.
 * Inner Class Value will represent Heap.
 *
 * @author Zhao Liu
 */
public class Memory {
    /**
     * This is an inner class.
     * Class Value will represent Heap in our program.
     */
    private static class Value {
        public Core type;
        public int intValue;
        public int[] arrayValue;
    }

    // This is the only one Instantiation of this class.
    private static Memory instance = null;

    /**
     * In order to implement Singleton pattern, make constructor private.
     */
    private Memory() {
    }

    /**
     * Singleton pattern
     *
     * @return the single instance
     */
    public static synchronized Memory getInstance() {
        if (instance == null) {
            instance = new Memory();
            Stack<Map<String, Value>> mainFrameVariablesStack = new Stack<>();
            instance.localVariablesStack.push(mainFrameVariablesStack);
        }
        return instance;
    }

    private Map<String, Value> global = new HashMap<>();
    private Stack<Stack<Map<String, Value>>> localVariablesStack = new Stack<>();
    private Queue<Integer> inputDataQueue;

    // Use this flag to keep track of when we finish the DeclSeq
    private boolean declSeqFinished = false;
    private boolean executingFunction = false;

    public void pushNewVariableStack() {
        Stack<Map<String, Value>> newVariablesStack = new Stack<>();
        localVariablesStack.push(newVariablesStack);
    }

    public void popVariableStack() {
        localVariablesStack.pop();
    }

    /**
     * @param variable
     * @param type     only 2 options, integer or array
     */
    public void allocate(Core type, String variable) {
        boolean isExist = isExist(variable);
        if (isExist) {
            System.out.println("ERROR: Variable " + variable + " has been doubly-declared!!!");
            System.exit(1);
        }

        Value valueHeap = new Value();
        valueHeap.type = type;
        if (type == Core.INTEGER) {
            valueHeap.intValue = 0;
        }

        if (declSeqFinished || executingFunction) {
            Map<String, Value> item = new HashMap<>();
            item.put(variable, valueHeap);

            Stack<Map<String, Value>> localVariables = localVariablesStack.peek();

            localVariables.push(item);
        } else {
            global.put(variable, valueHeap);
        }
    }

    /**
     * Initializing the array by input size.
     *
     * @param variable variable name
     * @param size     array size
     */
    public void initializeArray(String variable, int size) {
        boolean isExist = isExist(variable);
        if (!isExist) {
            System.out.println("ERROR: Variable " + variable + " has not been declared!!!");
            System.exit(1);
        }

        boolean isInitialized = false;
        Stack<Map<String, Value>> localVariables = localVariablesStack.peek();

        for (Map<String, Value> temp : localVariables) {
            Value tempValue = temp.get(variable);
            if (tempValue != null) {
                tempValue.arrayValue = new int[size];
                isInitialized = true;
                break;
            }
        }
        if (!isInitialized) {
            Value tempValue = global.get(variable);
            tempValue.arrayValue = new int[size];
        }
    }

    /**
     * Look up based on input value "variable", change the "intValue" to the input value "value" passed in
     *
     * @param variable input variable
     * @param value    input value
     */
    public void update(String variable, int value) {
        boolean isExist = isExist(variable);
        if (!isExist) {
            System.out.println("ERROR: Variable " + variable + " has not been declared!!!");
            System.exit(1);
        }

        boolean isUpdated = false;
        Stack<Map<String, Value>> localVariables = localVariablesStack.peek();

        for (Map<String, Value> temp : localVariables) {
            Value tempValue = temp.get(variable);
            if (tempValue != null) {
                updateHeapValue(tempValue, 0, value);
                isUpdated = true;
                break;
            }
        }
        if (!isUpdated) {
            Value tempValue = global.get(variable);
            updateHeapValue(tempValue, 0, value);
        }
    }

    /**
     * Update the value of index of array
     *
     * @param variable variable name
     * @param index    int
     * @param value    the value
     */
    public void updateArray(String variable, int index, int value) {
        boolean isExist = isExist(variable);
        if (!isExist) {
            System.out.println("ERROR: Variable " + variable + " has not been declared!!!");
            System.exit(1);
        }

        boolean isUpdated = false;
        Stack<Map<String, Value>> localVariables = localVariablesStack.peek();

        for (Map<String, Value> temp : localVariables) {
            Value tempValue = temp.get(variable);
            if (tempValue != null) {
                updateHeapValue(tempValue, index, value);
                isUpdated = true;
                break;
            }
        }
        if (!isUpdated) {
            Value tempValue = global.get(variable);
            updateHeapValue(tempValue, index, value);
        }
    }

    /**
     * If "valueHeap.type" == INTEGER, then update intValue.
     * If "valueHeap.type" == ARRAY, then update arrayValue by index position.
     *
     * @param valueHeap represents an object in heap
     * @param index     int
     * @param value     the value
     */
    private void updateHeapValue(Value valueHeap, int index, int value) {
        if (valueHeap.type == Core.INTEGER) {
            valueHeap.intValue = value;
        } else {
            if (valueHeap.arrayValue == null) {
                System.out.println("ERROR: Array has not been initialized!!!");
                System.exit(1);
            }
            int size = valueHeap.arrayValue.length;
            if (index >= size) {
                System.out.println("ERROR: Array has reached out of range!!!");
                System.exit(1);
            }

            valueHeap.arrayValue[index] = value;
        }
    }

    /**
     * Find value from "local" or "global" by input variable
     *
     * @param variable variable name
     * @return the value based on variable name
     */
    public int find(String variable) {
        boolean isExist = isExist(variable);
        if (!isExist) {
            System.out.println("ERROR: Variable " + variable + " has not been declared!!!");
            System.exit(1);
        }

        Stack<Map<String, Value>> localVariables = localVariablesStack.peek();
        Value value = null;
        int result;
        for (Map<String, Value> temp : localVariables) {
            Value tempValue = temp.get(variable);
            if (tempValue != null) {
                value = tempValue;
                break;
            }
        }
        if (value == null) {
            value = global.get(variable);
        }

        if (value.type == Core.INTEGER) {
            result = value.intValue;
        } else {
            if (value.arrayValue == null) {
                System.out.println("ERROR: Array " + variable + " has not been initialized!!!");
                System.exit(1);
            }
            result = value.arrayValue[0];
        }

        return result;
    }

    /**
     * Find value from "local" or "global" by input variable name and index.
     *
     * @param variable variable name
     * @param index    int
     * @return the value from array based on variable name and index
     */
    public int findArrayByIndex(String variable, int index) {
        boolean isExist = isExist(variable);
        if (!isExist) {
            System.out.println("ERROR: Variable " + variable + " has not been declared!!!");
            System.exit(1);
        }

        Stack<Map<String, Value>> localVariables = localVariablesStack.peek();
        Value value = null;
        int result;
        for (Map<String, Value> temp : localVariables) {
            Value tempValue = temp.get(variable);
            if (tempValue != null) {
                value = tempValue;
                break;
            }
        }
        if (value == null) {
            value = global.get(variable);
        }

        if (value.arrayValue == null) {
            System.out.println("ERROR: Array " + variable + " has not been initialized!!!");
            System.exit(1);
        }
        int size = value.arrayValue.length;
        if (index >= size) {
            System.out.println("ERROR: Array " + variable + "[" + index + "] has reached out of range!!!");
            System.exit(1);
        }

        result = value.arrayValue[index];

        return result;
    }

    /**
     * the left-hand side will have the same reference value as the id on the right-hand side
     * both variables "point" to the same array
     *
     * @param lhsVariable left-hand side variable
     * @param rhsVariable right-hand side variable
     */
    public void copyBySharing(String lhsVariable, String rhsVariable) {
        boolean isLhsExist = isExist(lhsVariable);
        if (!isLhsExist) {
            System.out.println("ERROR: Variables " + lhsVariable + " has not been declared!!!");
            System.exit(1);
        }

        boolean isCopied = false;
        Value rhsValue = null;
        Stack<Map<String, Value>> rightHandSideVariables;
        Stack<Map<String, Value>> localVariables = localVariablesStack.pop();

        // If current Call Stack is located at main frame, we only have 1 variable stack.
        // So right hand-side value can only come from this stack.
        if (localVariablesStack.size() == 0) {
            rightHandSideVariables = localVariables;
        } else {
            // If current Call Stack has multi frames/stacks, so we need to use the second stack to find right hand-side value.
            rightHandSideVariables = localVariablesStack.peek();
        }

        boolean isRhsExist = isExistByStack(rightHandSideVariables, rhsVariable);
        if (!isRhsExist) {
            System.out.println("ERROR: Variables " + rhsVariable + " has not been declared!!!");
            System.exit(1);
        }

        for (Map<String, Value> temp : rightHandSideVariables) {
            Value tempValue = temp.get(rhsVariable);
            if (tempValue != null) {
                rhsValue = tempValue;
                break;
            }
        }
        if (rhsValue == null) {
            rhsValue = global.get(rhsVariable);
        }

        // Push the variable stack back to localVariablesStack
        localVariablesStack.push(localVariables);

        for (Map<String, Value> temp : localVariables) {
            boolean isContain = temp.containsKey(lhsVariable);
            if (isContain) {
                temp.replace(lhsVariable, rhsValue);
                isCopied = true;
                break;
            }
        }
        if (!isCopied) {
            global.replace(lhsVariable, rhsValue);
        }
    }

    /**
     * checking whether variable exists in "local" or "global"
     *
     * @param variable variable name
     * @return true for exist, false for not exist
     */
    private boolean isExist(String variable) {
        boolean isFound = false;
        Stack<Map<String, Value>> localVariables = localVariablesStack.peek();

        for (Map<String, Value> temp : localVariables) {
            if (temp.containsKey(variable)) {
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            isFound = global.containsKey(variable);
        }

        return isFound;
    }

    private boolean isExistByStack(Stack<Map<String, Value>> variableStack, String variable) {
        boolean isFound = false;

        for (Map<String, Value> temp : variableStack) {
            if (temp.containsKey(variable)) {
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            isFound = global.containsKey(variable);
        }

        return isFound;
    }

    public int localSize() {
        Stack<Map<String, Value>> localVariables = localVariablesStack.peek();
        return localVariables.size();
    }

    public int globalSize() {
        return global.size();
    }

    public void popLocalElement() {
        Stack<Map<String, Value>> localVariables = localVariablesStack.peek();
        localVariables.pop();
    }

    public boolean isDeclSeqFinished() {
        return declSeqFinished;
    }

    public void setDeclSeqFinished(boolean declSeqFinished) {
        this.declSeqFinished = declSeqFinished;
    }

    public boolean isExecutingFunction() {
        return executingFunction;
    }

    public void setExecutingFunction(boolean executingFunction) {
        this.executingFunction = executingFunction;
    }

    public Queue<Integer> getInputDataQueue() {
        return inputDataQueue;
    }

    public void setInputDataQueue(Queue<Integer> inputDataQueue) {
        this.inputDataQueue = inputDataQueue;
    }
}
