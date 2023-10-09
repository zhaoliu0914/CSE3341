import java.util.HashMap;
import java.util.Stack;

/**
 * Simulating memory (Stack and Heap) for our variables.
 * There are 3 “regions” of memory: Global, Local, and Heap.
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

    public static synchronized Memory getInstance() {
        if (instance == null) {
            instance = new Memory();
        }
        return instance;
    }

    private HashMap<String, Value> global = new HashMap<>();
    private Stack<HashMap<String, Value>> local = new Stack<>();

    // Use this flag to keep track of when we finish the DeclSeq
    private boolean declSeqFinished = false;

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

        if (declSeqFinished) {
            HashMap<String, Value> item = new HashMap<>();
            item.put(variable, valueHeap);

            local.push(item);
        } else {
            global.put(variable, valueHeap);
        }
    }

    public void initializeArray(String variable, int size) {
        boolean isExist = isExist(variable);
        if (isExist) {
            if (declSeqFinished) {
                for (HashMap<String, Value> temp : local) {
                    Value tempValue = temp.get(variable);
                    if(tempValue != null){
                        tempValue.arrayValue = new int[size];
                        break;
                    }
                }
            }
            Value tempValue = global.get(variable);
            tempValue.arrayValue = new int[size];
        } else {
            System.out.println("ERROR: Variable " + variable + " has not been declared!!!");
            System.exit(1);
        }
    }

    /**
     * Look up based on input value "variable", change the "intValue" to the input value "value" passed in
     * if declSeqFinished == false, save all the input value to "global".
     * if declSeqFinished == true, save all the input value to "local".
     *
     * @param variable input variable
     * @param value    input value
     */
    public void update(String variable, int value) {
        boolean isExist = isExist(variable);
        if (isExist) {
            if (declSeqFinished) {
                for (HashMap<String, Value> temp : local) {
                    Value tempValue = temp.get(variable);
                    if (tempValue != null) {
                        updateHeapValue(tempValue, value);
                        break;
                    }
                }
            }
            Value tempValue = global.get(variable);
            updateHeapValue(tempValue, value);
        } else {
            System.out.println("ERROR: Variable " + variable + " has not been declared!!!");
            System.exit(1);
        }
    }

    public void updateArray(String variable, int index, int value) {
        boolean isExist = isExist(variable);
        if (isExist) {
            if (declSeqFinished) {
                for (HashMap<String, Value> temp : local) {
                    Value tempValue = temp.get(variable);
                    if (tempValue != null) {
                        tempValue.arrayValue[index] = value;
                        break;
                    }
                }
            }
            Value tempValue = global.get(variable);
            tempValue.arrayValue[index] = value;
        } else {
            System.out.println("ERROR: Variable " + variable + " has not been declared!!!");
            System.exit(1);
        }
    }

    private void updateHeapValue(Value valueHeap, int value) {
        if (valueHeap.type == Core.INTEGER) {
            valueHeap.intValue = value;
        } else {
            valueHeap.arrayValue[0] = value;
        }
    }

    public void find(String variable) {

    }

    private boolean isExist(String variable) {
        if (declSeqFinished) {
            for (HashMap<String, Value> temp : local) {
                Value tempValue = temp.get(variable);
                if (tempValue != null) {
                    return true;
                }
            }
        }

        Value temp = global.get(variable);
        if (temp != null) {
            return true;
        }

        return false;
    }

    public boolean isDeclSeqFinished() {
        return declSeqFinished;
    }

    public void setDeclSeqFinished(boolean declSeqFinished) {
        this.declSeqFinished = declSeqFinished;
    }
}
