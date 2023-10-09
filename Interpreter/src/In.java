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
    public void parse(Queue<Object> tokenQueue) {
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
    public void semanticChecking(Stack<Variable> variableStack) {
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
