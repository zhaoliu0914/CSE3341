import java.util.*;

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
        public int referenceCount;
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
    private int totalObjects = 0;

    // Use this flag to keep track of when we finish the DeclSeq
    private boolean declSeqFinished = false;
    private boolean executingFunction = false;
    private boolean initializeFormalParams = false;

    public void pushNewVariableStack() {
        Stack<Map<String, Value>> newVariablesStack = new Stack<>();
        localVariablesStack.push(newVariablesStack);
    }

    public void popVariableStack() {
        // Garbage collection all the Variables and Objects from the top Stack/Frame
        Stack<Map<String, Value>> topStack = localVariablesStack.peek();
        for (Map<String, Value> localVariableMap : topStack) {
            for (Map.Entry<String, Value> entry : localVariableMap.entrySet()) {
                Value tempValue = entry.getValue();

                // for Garbage Collection.
                if (tempValue != null && tempValue.type == Core.ARRAY) {
                    tempValue.referenceCount--;
                    if (tempValue.referenceCount == 0) {
                        totalObjects--;
                        System.out.println("gc:" + totalObjects);
                    }
                }
            }
        }

        // pop the top Stack/Frame
        localVariablesStack.pop();
    }

    /**
     * @param variable
     * @param type     only 2 options, integer or array
     */
    public void allocate(Core type, String variable) {
        boolean isExist = false;
        if (executingFunction) {
            isExist = isExistLocal(variable);
        } else {
            isExist = isExistGlobalAndLocal(variable);
        }
        if (isExist) {
            System.out.println("ERROR: Variable " + variable + " has been doubly-declared!!!");
            System.exit(1);
        }

        Value valueHeap = null;
        if (type == Core.INTEGER) {
            valueHeap = new Value();
            valueHeap.type = Core.INTEGER;
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
        boolean isExist = isExistGlobalAndLocal(variable);
        if (!isExist) {
            System.out.println("ERROR: Variable " + variable + " has not been declared!!!");
            System.exit(1);
        }

        boolean isInitialized = false;
        Stack<Map<String, Value>> localVariables = localVariablesStack.peek();

        for (Map<String, Value> temp : localVariables) {
            boolean isContain = temp.containsKey(variable);
            if (isContain) {
                // for Garbage Collection.
                Value tempValue = temp.get(variable);
                if (tempValue != null) {
                    tempValue.referenceCount--;
                    if (tempValue.referenceCount == 0) {
                        totalObjects--;
                        System.out.println("gc:" + totalObjects);
                    }
                }

                Value newValue = new Value();
                newValue.type = Core.ARRAY;
                // for Garbage Collection
                newValue.referenceCount = 1;
                newValue.arrayValue = new int[size];
                totalObjects++;

                temp.replace(variable, newValue);

                // for Garbage Collection
                System.out.println("gc:" + totalObjects);

                isInitialized = true;
                break;
            }
        }
        if (!isInitialized) {
            Value tempValue = global.get(variable);

            // for Garbage Collection.
            if (tempValue != null) {
                tempValue.referenceCount--;
                if (tempValue.referenceCount == 0) {
                    totalObjects--;
                    System.out.println("gc:" + totalObjects);
                }
            }

            Value newValue = new Value();
            newValue.type = Core.ARRAY;
            // for Garbage Collection
            newValue.referenceCount = 1;
            newValue.arrayValue = new int[size];
            totalObjects++;

            global.replace(variable, newValue);

            // for Garbage Collection
            System.out.println("gc:" + totalObjects);
        }
    }

    /**
     * Look up based on input value "variable", change the "intValue" to the input value "value" passed in
     *
     * @param variable input variable
     * @param value    input value
     */
    public void update(String variable, int value) {
        boolean isExist = isExistGlobalAndLocal(variable);
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
        boolean isExist = isExistGlobalAndLocal(variable);
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
        if (valueHeap != null) {
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
        } else {
            System.out.println("ERROR: Array has not been initialized!!!");
            System.exit(1);
        }
    }

    /**
     * Find value from "local" or "global" by input variable
     *
     * @param variable variable name
     * @return the value based on variable name
     */
    public int find(String variable) {
        boolean isExist = isExistGlobalAndLocal(variable);
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
        boolean isExist = isExistGlobalAndLocal(variable);
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
        boolean isLhsExist = isExistGlobalAndLocal(lhsVariable);
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
        boolean isRhsExist;
        if (initializeFormalParams) {
            if (localVariablesStack.size() == 0) {
                rightHandSideVariables = localVariables;
            } else {
                // If current Call Stack has multi frames/stacks, so we need to use the second stack to find right hand-side value.
                rightHandSideVariables = localVariablesStack.peek();
            }
            isRhsExist = isExistByStack(rightHandSideVariables, rhsVariable);

        } else {
            rightHandSideVariables = localVariables;
            isRhsExist = isExistByStack(rightHandSideVariables, rhsVariable);

            if (!isRhsExist) {
                rightHandSideVariables = localVariablesStack.peek();

                isRhsExist = isExistByStack(rightHandSideVariables, rhsVariable);

            }
        }
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
                // for Garbage Collection.
                Value tempValue = temp.get(lhsVariable);
                if (tempValue != null) {
                    tempValue.referenceCount--;
                    if (tempValue.referenceCount == 0) {
                        totalObjects--;
                        System.out.println("gc:" + totalObjects);
                    }
                }

                temp.replace(lhsVariable, rhsValue);
                if (rhsValue != null) {
                    rhsValue.referenceCount++;
                }

                isCopied = true;
                break;
            }
        }
        if (!isCopied) {
            // for Garbage Collection.
            Value tempValue = global.get(lhsVariable);
            if (tempValue != null) {
                tempValue.referenceCount--;
                if (tempValue.referenceCount == 0) {
                    totalObjects--;
                    System.out.println("gc:" + totalObjects);
                }
            }

            global.replace(lhsVariable, rhsValue);
            if (rhsValue != null) {
                rhsValue.referenceCount++;
            }
        }
    }

    public void emptyMemory() {
        while (localVariablesStack.size() > 0) {
            this.popVariableStack();
        }

        // Garbage collection all the Variables and Objects for the Global
        for (Map.Entry<String, Value> entry : global.entrySet()) {
            String variable = entry.getKey();
            Value tempValue = entry.getValue();

            // for Garbage Collection.
            if (tempValue != null && tempValue.type == Core.ARRAY) {
                tempValue.referenceCount--;
                if (tempValue.referenceCount == 0) {
                    totalObjects--;
                    System.out.println("gc:" + totalObjects);
                }
            }

            global.replace(variable, null);
        }
    }

    /**
     * checking whether variable exists in "local" or "global"
     *
     * @param variable variable name
     * @return true for exist, false for not exist
     */
    private boolean isExistLocal(String variable) {
        boolean isFound = false;
        Stack<Map<String, Value>> localVariables = localVariablesStack.peek();

        for (Map<String, Value> temp : localVariables) {
            if (temp.containsKey(variable)) {
                isFound = true;
                break;
            }
        }

        return isFound;
    }

    /**
     * checking whether variable exists in "local" or "global"
     *
     * @param variable variable name
     * @return true for exist, false for not exist
     */
    private boolean isExistGlobalAndLocal(String variable) {
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

    public boolean isInitializeFormalParams() {
        return initializeFormalParams;
    }

    public void setInitializeFormalParams(boolean initializeFormalParams) {
        this.initializeFormalParams = initializeFormalParams;
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
