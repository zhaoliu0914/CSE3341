import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * There is only 1 case available.
 * <decl-array> ==> array id ;
 * <p>
 * This case is quiet simple. We only need to check whether missing chars from the statement.
 *
 * @author Zhao Liu
 */
public class DeclarationArray {
    private Core arrayKeyword;
    private String variable;
    private Core semicolon;

    /**
     * The grammar is: <decl-array> ==> array id ;
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue, Map<String, Function> functionCheckingMap) {
        if (tokenQueue.poll() != Core.ARRAY) {
            System.out.println("ERROR: missing keyword 'array' for declaration statement.");
            System.exit(1);
        }
        arrayKeyword = Core.ARRAY;

        tokenQueue.poll();
        variable = (String) tokenQueue.poll();

        if (tokenQueue.poll() != Core.SEMICOLON) {
            System.out.println("ERROR: missing semicolon symbol ';' for declaration statement.");
            System.exit(1);
        }
        semicolon = Core.SEMICOLON;
    }

    /**
     * It will perform a Semantic Checking.
     * There are several types of Semantic Errors need to be checked.
     * 1. Undeclared variables or out out of scope.
     * 2. Doubly-declared variables.
     * 3. variable type (integer or array) has to be matched when performing an assign operation.
     *
     * This class DeclarationArray may only occur "Doubly-declared" variables.
     *
     * @param variableStack contains all declared variables
     */
    public void semanticChecking(Stack<Variable> variableStack, Map<String, Function> functionMap) {
        for (Variable temp : variableStack) {
            if (temp.getName().equals(variable)) {
                System.out.println("ERROR: Array variable " + variable + " has been doubly-declared!!!");
                System.exit(1);
            }
        }

        Variable attribute = new Variable();
        attribute.setName(variable);
        attribute.setType(Core.ARRAY);
        variableStack.push(attribute);
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     */
    public void execute(Memory memory, Map<String, Function> functionMap) {
        memory.allocate(Core.ARRAY, variable);
    }

    /**
     * produce "pretty" code with the appropriate indentation
     *
     * @param indent the number of spaces which need to be print
     */
    public void print(int indent) {
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }

        System.out.print(arrayKeyword.toString().toLowerCase() + " ");
        System.out.print(variable);
        System.out.println(";");
    }

}
