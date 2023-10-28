import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * There is only 1 case available.
 * <in> ==> in ( id ) ;
 * <p>
 * This case is quiet simple. We only need to check whether missing chars from the statement.
 *
 * @author Zhao Liu
 */
public class In {
    private Core inKeyword;
    private Core leftParenthesis;
    private Core rightParenthesis;
    private String variable;
    private Core semicolon;

    /**
     * The grammar is: <in> ==> in ( id ) ;
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue, Map<String, Function> functionCheckingMap) {
        if (tokenQueue.poll() != Core.IN) {
            System.out.println("ERROR: missing keyword 'in'!!!");
            System.exit(1);
        }
        inKeyword = Core.IN;

        if (tokenQueue.poll() != Core.LPAREN) {
            System.out.println("ERROR: missing symbol '('!!!");
            System.exit(1);
        }
        leftParenthesis = Core.LPAREN;

        tokenQueue.poll();
        variable = (String) tokenQueue.poll();

        if (tokenQueue.poll() != Core.RPAREN) {
            System.out.println("ERROR: missing symbol ')'!!!");
            System.exit(1);
        }
        rightParenthesis = Core.RPAREN;

        if (tokenQueue.poll() != Core.SEMICOLON) {
            System.out.println("ERROR: missing semicolon symbol ';'");
            System.exit(1);
        }
        semicolon = Core.SEMICOLON;
    }

    /**
     * It will perform a Semantic Checking.
     * There are several types of Semantic Errors need to be checked.
     * 1. Undeclared variables or out of scope.
     * 2. Doubly-declared variables.
     * 3. variable type (integer or array) has to be matched when performing an assign operation.
     *
     * @param variableStack contains all declared variables
     */
    public void semanticChecking(Stack<Variable> variableStack, Map<String, Function> functionCheckingMap) {
        boolean isLHSVariableExist = false;
        for (Variable temp : variableStack) {
            if (temp.getName().equals(variable)) {
                isLHSVariableExist = true;
                break;
            }
        }
        if (!isLHSVariableExist) {
            System.out.println("ERROR: the variable " + variable + " has not been declared before!!!");
            System.exit(1);
        }
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     */
    public void execute(Memory memory, Map<String, Function> functionMap) {
        Queue<Integer> inputDataQueue = memory.getInputDataQueue();
        Integer value = inputDataQueue.poll();

        if (value == null) {
            System.out.println("ERROR: all values in the .data file have already been used!!!");
            System.exit(1);
        }

        memory.update(variable, value);
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

        System.out.print(inKeyword.toString().toLowerCase());
        System.out.print("(");
        System.out.print(variable);
        System.out.print(")");
        System.out.println(";");
    }

}
