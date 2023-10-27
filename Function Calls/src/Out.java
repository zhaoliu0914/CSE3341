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
public class Out {
    private Core outKeyword;
    private Core leftParenthesis;
    private Core rightParenthesis;
    private Expression expression;
    private Core semicolon;

    /**
     * The grammar is: <out> ==> out ( <expr> ) ;
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue) {
        if (tokenQueue.poll() != Core.OUT) {
            System.out.println("ERROR: missing keyword 'out'!!!");
            System.exit(1);
        }
        outKeyword = Core.OUT;

        if (tokenQueue.poll() != Core.LPAREN) {
            System.out.println("ERROR: missing symbol '('!!!");
            System.exit(1);
        }
        leftParenthesis = Core.LPAREN;

        expression = new Expression();
        expression.parse(tokenQueue);

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
        expression.semanticChecking(variableStack);
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     *
     * The grammar is "<out> ::= out ( <expr> ) ;"
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     */
    public void execute(Memory memory) {
        int value = expression.execute(memory);
        System.out.println(value);
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

        System.out.print(outKeyword.toString().toLowerCase());
        System.out.print("(");
        expression.print(0);
        System.out.print(")");
        System.out.println(";");
    }

}
