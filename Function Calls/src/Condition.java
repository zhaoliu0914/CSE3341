import java.util.Map;
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
    public void parse(Queue<Object> tokenQueue, Map<String, Function> functionMap) {
        if (tokenQueue.peek() == Core.NOT) {
            // If the first word of token sequence is "not", then it is "<cond> ==> not <cond>"
            // so the second token must be "<cond>"
            tokenQueue.poll();
            notKeyword = Core.NOT;

            condition = new Condition();
            condition.parse(tokenQueue, functionMap);

        } else {
            // Otherwise, the first token would be "<cmpr>"
            compare = new Compare();
            compare.parse(tokenQueue, functionMap);

            if (tokenQueue.peek() == Core.OR) {
                // If the second token is "or", then it is "<cmpr> or <cond>"
                tokenQueue.poll();
                orKeyword = Core.OR;

                condition = new Condition();
                condition.parse(tokenQueue, functionMap);
            } else if (tokenQueue.peek() == Core.AND) {
                // If the second token is "and", then it is "<cmpr> and <cond>"
                tokenQueue.poll();
                andKeyword = Core.AND;

                condition = new Condition();
                condition.parse(tokenQueue, functionMap);
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
    public void semanticChecking(Stack<Variable> variableStack, Map<String, Function> functionCheckingMap) {
        if (compare != null) {
            compare.semanticChecking(variableStack, functionCheckingMap);
        }
        if (condition != null) {
            condition.semanticChecking(variableStack, functionCheckingMap);
        }
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     *
     * The grammar is "<cond> ::= <cmpr> | not <cond> | <cmpr> or <cond> | <cmpr> and <cond>",
     * so there are 4 cases need to handle.
     *
     * If "notKeyword" != null, it indicates "<cond> ::= not <cond>"
     * If "orKeyword" != null, it indicates "<cond> ::= <cmpr> or <cond>"
     * If "andKeyword" != null, it indicates "<cond> ::= <cmpr> and <cond>"
     * otherwise, it is "<cond> ::= <cmpr>"
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     * @return true or false, the result of condition
     */
    public boolean execute(Memory memory, Map<String, Function> functionMap) {
        boolean result = false;

        if (notKeyword != null) {
            // Handle case for "<cond> ::= not <cond>"
            // Negate the value of "<cond>"
            boolean conditionValue = condition.execute(memory, functionMap);
            if (!conditionValue) {
                result = true;
            }

        } else if (orKeyword != null) {
            // Handle case for "<cond> ::= <cmpr> or <cond>"
            // either "<cmpr>" or "<cond>" is true, return true
            boolean compareValue = compare.execute(memory, functionMap);
            boolean conditionValue = condition.execute(memory, functionMap);
            if (compareValue || conditionValue) {
                result = true;
            }

        } else if (andKeyword != null) {
            // Handle case for "<cond> ::= <cmpr> or <cond>"
            // either "<cmpr>" or "<cond>" is true, return true
            boolean compareValue = compare.execute(memory, functionMap);
            boolean conditionValue = condition.execute(memory, functionMap);
            if (compareValue && conditionValue) {
                result = true;
            }

        } else {
            // Handle case for "<cond> ::= <cmpr>"
            // return the result of "<cmpr>"
            result = compare.execute(memory, functionMap);
        }

        return result;
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
