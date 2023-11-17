import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * There are 2 different types of "Compare" operations available.
 * <cmpr> ==> <expr> = <expr>
 * <cmpr> ==> <expr> < <expr>
 * <p>
 * Symbols "=" and "<" indicate which situation we are working on.
 * Variables "equal" and "lessThan" will be assigned value when if it's the case.
 *
 * @author Zhao Liu
 */
public class Compare {
    private Core equal;
    private Core lessThan;
    private Expression leftExpression;
    private Expression rightExpression;

    /**
     * The grammar is: <cmpr> ==> <expr> = <expr> | <expr> < <expr>
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue, Map<String, Function> functionMap) {
        leftExpression = new Expression();
        leftExpression.parse(tokenQueue, functionMap);

        if (tokenQueue.peek() == Core.EQUAL) {
            tokenQueue.poll();
            equal = Core.EQUAL;
        } else if (tokenQueue.peek() == Core.LESS) {
            tokenQueue.poll();
            lessThan = Core.LESS;
        } else {
            System.out.println("ERROR: the compare operation only accepts symbol '=' or '<'!!!");
            System.exit(1);
        }

        rightExpression = new Expression();
        rightExpression.parse(tokenQueue, functionMap);
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
        leftExpression.semanticChecking(variableStack, functionCheckingMap);
        rightExpression.semanticChecking(variableStack, functionCheckingMap);
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     * <p>
     * "Compare" is going to compare the result of "leftExpression" and the result of "rightExpression".
     * The grammar is "<cmpr> ::= <expr> = <expr>"
     * If "equal" is not null, it will check whether left value is equal to right value.
     * <p>
     * The grammar is "<cmpr> ::= <expr> < <expr>"
     * If "lessThan" is not null, it will check whether left value is less than right value.
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     * @return true or false, the result of compare two expression
     */
    public boolean execute(Memory memory, Map<String, Function> functionMap) {
        boolean result = false;
        int leftValue = leftExpression.execute(memory, functionMap);
        int rightValue = rightExpression.execute(memory, functionMap);

        if (equal != null) {
            // Handle case for ""<cmpr> ::= <expr> = <expr>""
            // It will check whether left value is equal to right value.
            if (leftValue == rightValue) {
                result = true;
            }

        } else {
            // Handle case for "<cmpr> ::= <expr> < <expr>"
            // It will check whether left value is less than right value.
            if (leftValue < rightValue) {
                result = true;
            }
        }
        return result;
    }


    /**
     * produce "pretty" code with the appropriate indentation
     *
     * @param indent the number of spaces which need to be print
     */
    public void print(int indent) {
        leftExpression.print(0);
        if (equal != null) {
            System.out.print(" = ");
        } else {
            System.out.print(" < ");
        }
        rightExpression.print(0);
    }

}
