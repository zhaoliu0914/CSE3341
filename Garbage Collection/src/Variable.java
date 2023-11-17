/**
 * Store variable's name, type, and value for performing Semantic Checking
 *
 * @author Zhao Liu
 */
public class Variable {
    private Core type;
    private String name;
    private int integerValue;
    private int[] arrayValue;

    public Core getType() {
        return type;
    }

    public void setType(Core type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(int integerValue) {
        this.integerValue = integerValue;
    }

    public int[] getArrayValue() {
        return arrayValue;
    }

    public void setArrayValue(int[] arrayValue) {
        this.arrayValue = arrayValue;
    }
}
