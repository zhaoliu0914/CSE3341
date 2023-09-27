import java.util.Queue;
import java.util.Stack;

/**
 * There is only 1 case available.
 * <decl-integer> ==> integer id ;
 * <p>
 * This case is quiet simple. We only need to check whether missing chars from the statement.
 *
 * @author Zhao Liu
 */
public class DeclarationInteger {
    private Core integerKeyword;
    private String variable;
    private Core semicolon;

    /**
     * The grammar is: <decl-integer> ==> integer id ;
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue) {
        if (tokenQueue.poll() != Core.INTEGER) {
            System.out.println("ERROR: missing keyword 'integer' for declaration statement.");
            System.exit(1);
        }
        integerKeyword = Core.INTEGER;

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
     * 1. Undeclared variables or out of scope.
     * 2. Doubly-declared variables.
     * 3. variable type (integer or array) has to be matched when performing an assign operation.
     * <p>
     * This class DeclarationInteger may only occur "Doubly-declared" variables.
     *
     * @param variableStack contains all declared variables
     */
    public void semanticChecking(Stack<Variable> variableStack) {
        for (Variable temp : variableStack) {
            if (temp.getName().equals(variable)) {
                System.out.println("ERROR: Integer variable " + variable + " has been doubly-declared!!!");
                System.exit(1);
            }
        }

        Variable attribute = new Variable();
        attribute.setName(variable);
        attribute.setType(Core.INTEGER);
        variableStack.push(attribute);
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

        System.out.print(integerKeyword.toString().toLowerCase() + " ");
        System.out.print(variable);
        System.out.println(";");
    }

}
