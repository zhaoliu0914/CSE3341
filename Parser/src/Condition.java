import java.util.Queue;
import java.util.Stack;

/**
 * There are 4 different types of "Condition" operations available.
 * <cond> ==> <cmpr>
 * <cond> ==> not <cond>
 * <cond> ==> <cmpr> or <cond>
 * <cond> ==> <cmpr> and <cond>
 * <p>
 * If the first word of token sequence is "not", then it is "<cond> ==> not <cond>", so the second token must be "<cond>"
 * <p>
 * Otherwise, the first token would be "<cmpr>"
 * If the second token is "or", then it is "<cmpr> or <cond>"
 * If the second token is "and", then it is "<cmpr> and <cond>"
 *
 * @author Zhao Liu
 */
public class Condition {
    private Core notKeyword;
    private Core orKeyword;
    private Core andKeyword;
    private Compare compare;
    private Condition condition;

    /**
     * The grammar is: <cond> ==> <cmpr> | not <cond> | <cmpr> or <cond> | <cmpr> and <cond>
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue) {
        if (tokenQueue.peek() == Core.NOT) {
            // If the first word of token sequence is "not", then it is "<cond> ==> not <cond>"
            // so the second token must be "<cond>"
            tokenQueue.poll();
            notKeyword = Core.NOT;

            condition = new Condition();
            condition.parse(tokenQueue);

        } else {
            // Otherwise, the first token would be "<cmpr>"
            compare = new Compare();
            compare.parse(tokenQueue);

            if (tokenQueue.peek() == Core.OR) {
                // If the second token is "or", then it is "<cmpr> or <cond>"
                tokenQueue.poll();
                orKeyword = Core.OR;

                condition = new Condition();
                condition.parse(tokenQueue);
            } else if (tokenQueue.peek() == Core.AND) {
                // If the second token is "and", then it is "<cmpr> and <cond>"
                tokenQueue.poll();
                andKeyword = Core.AND;

                condition = new Condition();
                condition.parse(tokenQueue);
            }
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
        if (compare != null) {
            compare.semanticChecking(variableStack);
        }
        if (condition != null) {
            condition.semanticChecking(variableStack);
        }
    }


    /**
     * produce "pretty" code with the appropriate indentation
     *
     * @param indent the number of spaces which need to be print
     */
    public void print(int indent) {
        if (notKeyword != null) {
            System.out.print(notKeyword.toString().toLowerCase() + " ");
            condition.print(0);

        } else if (orKeyword != null) {
            compare.print(0);
            System.out.print(" " + orKeyword.toString().toLowerCase() + " ");
            condition.print(0);

        } else if (andKeyword != null) {
            compare.print(0);
            System.out.print(" " + andKeyword.toString().toLowerCase() + " ");
            condition.print(0);

        } else {
            compare.print(0);
        }

    }

}
