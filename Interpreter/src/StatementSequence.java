import java.util.Queue;
import java.util.Stack;

/**
 * There are 2 different types of "StatementSequence".
 * <stmt-seq> ==> <stmt>
 * <stmt-seq> ==> <stmt><stmt-seq>
 * <p>
 * The most difficult one is going to figure out whether create singular <stmt> or create <stmt> following a <stmt-seq>.
 * It turns out we can peek the token after <stmt>.
 * If the token after <stmt> is keyword "end" or "else", we should not create any <stmt-seq>.
 * Otherwise, we have to create a <stmt-seq>.
 *
 * @author Zhao Liu
 */
public class StatementSequence {
    private Statement statement;
    private StatementSequence statementSequence;

    /**
     * The grammar is: <stmt-seq> ==> <stmt> | <stmt><stmt-seq>
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue) {
        statement = new Statement();
        statement.parse(tokenQueue);

        // If next token is keyword "end" or "else", it should not create any StatementSequence
        if (tokenQueue.peek() != Core.END && tokenQueue.peek() != Core.ELSE) {
            statementSequence = new StatementSequence();
            statementSequence.parse(tokenQueue);
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
        statement.semanticChecking(variableStack);

        if (statementSequence != null) {
            statementSequence.semanticChecking(variableStack);
        }
    }

    /**
     * produce "pretty" code with the appropriate indentation
     *
     * @param indent the number of spaces which need to be print
     */
    public void print(int indent) {
        statement.print(indent);

        if (statementSequence != null) {
            statementSequence.print(indent);
        }
    }
}
