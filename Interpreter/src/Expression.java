import java.util.Queue;
import java.util.Stack;

/**
 * There are 3 different types of "Expression" operations available.
 * <expr> ==> <term>
 * <expr> ==> <term> + <expr>
 * <expr> ==> <term> - <expr>
 * <p>
 * The first token has to be "<term>".
 * If the second token is symbol "+", then it is "<expr> ==> <term> + <expr>"
 * If the second token is symbol "-", then it is "<expr> ==> <term> - <expr>"
 *
 * @author Zhao Liu
 */
public class Expression {
    private Core add;
    private Core subtract;
    private Term term;
    private Expression expression;

    /**
     * The grammar is: <expr> ==> <term> | <term> + <expr> | <term> - <expr>
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue) {
        term = new Term();
        term.parse(tokenQueue);

        if (tokenQueue.peek() == Core.ADD) {
            // If the second token is symbol "+", then it is "<expr> ==> <term> + <expr>"
            tokenQueue.poll();
            add = Core.ADD;

            expression = new Expression();
            expression.parse(tokenQueue);
        } else if (tokenQueue.peek() == Core.SUBTRACT) {
            // If the second token is symbol "-", then it is "<expr> ==> <term> - <expr>"
            tokenQueue.poll();
            subtract = Core.SUBTRACT;

            expression = new Expression();
            expression.parse(tokenQueue);
        }

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
        term.semanticChecking(variableStack);

        if (expression != null) {
            expression.semanticChecking(variableStack);
        }
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     * <p>
     * There are three cases "<expr> ::= <term> | <term> + <expr> | <term> - <expr>"
     * If variable "add" != null, it indicates "<expr> ::= <term> + <expr>"
     * If variable "subtract" != null, it indicates "<expr> ::= <term> - <expr>"
     * Otherwise, it indicates "<expr> ::= <term>"
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     * @return the result of "<expr>"
     */
    public int execute(Memory memory) {
        int result = 0;

        int termValue = term.execute(memory);
        if (add != null) {
            // Handle case for "<expr> ::= <term> + <expr>"
            int expressionValue = expression.execute(memory);
            result = termValue + expressionValue;

        } else if (subtract != null) {
            // Handle case for "<expr> ::= <term> - <expr>"
            int expressionValue = expression.execute(memory);
            result = termValue - expressionValue;

        } else {
            // Handle case for "<expr> ::= <term>"
            result = termValue;
        }
        return result;
    }

    /**
     * produce "pretty" code with the appropriate indentation
     *
     * @param indent the number of spaces which need to be print
     */
    public void print(int indent) {
        term.print(0);
        if (add != null) {
            System.out.print("+");
            expression.print(0);
        } else if (subtract != null) {
            System.out.print("-");
            expression.print(0);
        }
    }

}
