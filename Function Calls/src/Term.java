import java.util.Queue;
import java.util.Stack;

/**
 * There are 3 different types of "Term" operations available.
 * <term> ==> <factor>
 * <term> ==> <factor> * <term>
 * <term> ==> <factor> / <term>
 * <p>
 * The first token has to be "<factor>".
 * If the second token is symbol "*", then it is "<term> ==> <factor> * <term>"
 * If the second token is symbol "/", then it is "<term> ==> <factor> / <term>"
 *
 * @author Zhao Liu
 */
public class Term {
    private Core multiply;
    private Core divide;
    private Factor factor;
    private Term term;

    /**
     * The grammar is: <term> ==> <factor> | <factor> * <term> | <factor> / <term>
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue) {
        factor = new Factor();
        factor.parse(tokenQueue);

        if (tokenQueue.peek() == Core.MULTIPLY) {
            // If the second token is symbol "*", then it is "<term> ==> <factor> * <term>"
            tokenQueue.poll();
            multiply = Core.MULTIPLY;

            term = new Term();
            term.parse(tokenQueue);
        } else if (tokenQueue.peek() == Core.DIVIDE) {
            // If the second token is symbol "/", then it is "<term> ==> <factor> / <term>"
            tokenQueue.poll();
            divide = Core.DIVIDE;

            term = new Term();
            term.parse(tokenQueue);
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
        factor.semanticChecking(variableStack);

        if (term != null) {
            term.semanticChecking(variableStack);
        }
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     * <p>
     * There are three cases "<term> ::= <factor> | <factor> * <term> | <factor> / <term>"
     * If variable "multiply" != null, it indicates "<term> ::= <factor> * <term>"
     * If variable "divide" != null, it indicates "<term> ::= <factor> / <term>"
     * Otherwise, it indicates "<term> ::= <factor>"
     * <p>
     * There is an additional semantic: when perform "<term> ::= <factor> / <term>", operation cannot divide by 0,
     * which means "<term>" can not be 0
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     * @return the result of "<term>"
     */
    public int execute(Memory memory) {
        int result = 0;

        int factorValue = factor.execute(memory);
        if (multiply != null) {
            // Handle case for "<term> ::= <factor> * <term>"
            int termValue = term.execute(memory);
            result = factorValue * termValue;

        } else if (divide != null) {
            // Handle case for "<term> ::= <factor> / <term>"
            int termValue = term.execute(memory);
            if (termValue == 0) {
                System.out.println("ERROR: can not divided by 0!!!");
                System.exit(1);
            }
            result = factorValue / termValue;

        } else {
            // Handle case for "<term> ::= <factor>"
            result = factorValue;
        }

        return result;
    }

    /**
     * produce "pretty" code with the appropriate indentation
     *
     * @param indent the number of spaces which need to be print
     */
    public void print(int indent) {
        factor.print(0);
        if (multiply != null) {
            System.out.print("*");
            term.print(0);
        } else if (divide != null) {
            System.out.print("/");
            term.print(0);
        }
    }

}
