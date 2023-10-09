import java.util.Queue;
import java.util.Stack;

/**
 * There are 2 different types of "IF" operations available.
 * <if> ==> if <cond> then <stmt-seq> end
 * <if> ==> if <cond> then <stmt-seq> else <stmt-seq> end
 * <p>
 * Keyword "else" distinguishes whether is a simple IF statement or IF-ELSE statement.
 * If it is "IF-ELSE" statement, variable elseKeyword will be assigned a value.
 *
 * @author Zhao Liu
 */
public class If {
    private Core ifKeyword;
    private Condition condition;
    private Core thenKeyword;
    private StatementSequence statementSequence;
    private Core elseKeyword;
    private StatementSequence elseStatementSequence;
    private Core endKeyword;

    /**
     * The grammar is: <if> ==> if <cond> then <stmt-seq> end | if <cond> then <stmt-seq> else <stmt-seq> end
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue) {
        if (tokenQueue.poll() != Core.IF) {
            System.out.println("ERROR: missing keyword 'if' for if statement!!!");
            System.exit(1);
        }
        ifKeyword = Core.IF;

        condition = new Condition();
        condition.parse(tokenQueue);

        if (tokenQueue.poll() != Core.THEN) {
            System.out.println("ERROR: missing keyword 'then' for if statement!!!");
            System.exit(1);
        }
        thenKeyword = Core.THEN;

        statementSequence = new StatementSequence();
        statementSequence.parse(tokenQueue);

        if (tokenQueue.peek() == Core.ELSE) {
            tokenQueue.poll();
            elseKeyword = Core.ELSE;

            elseStatementSequence = new StatementSequence();
            elseStatementSequence.parse(tokenQueue);

            if (tokenQueue.poll() != Core.END) {
                System.out.println("ERROR: missing keyword 'end' for if statement!!!");
                System.exit(1);
            }
            endKeyword = Core.END;

        } else if (tokenQueue.peek() == Core.END) {
            tokenQueue.poll();
            endKeyword = Core.END;

        } else {
            System.out.println("ERROR: wrong if statement structure, missing keyword 'else' or 'end'!!!");
            System.exit(1);
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
        int initialSize = variableStack.size();

        condition.semanticChecking(variableStack);
        statementSequence.semanticChecking(variableStack);

        // clean all variables in "if" statement from Stack.
        while (variableStack.size() > initialSize) {
            variableStack.pop();
        }

        if (elseKeyword != null) {
            elseStatementSequence.semanticChecking(variableStack);
        }

        // clean all variables in "if" statement from Stack.
        while (variableStack.size() > initialSize) {
            variableStack.pop();
        }
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     */
    public void execute(Memory memory) {

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

        System.out.print(ifKeyword.toString().toLowerCase() + " ");
        condition.print(0);
        System.out.println(" " + thenKeyword.toString().toLowerCase());

        int statementIndent = indent + 4;
        statementSequence.print(statementIndent);
        if (elseKeyword != null) {
            for (int i = 0; i < indent; i++) {
                System.out.print(" ");
            }
            System.out.println(elseKeyword.toString().toLowerCase());
            elseStatementSequence.print(statementIndent);
        }

        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        System.out.println(endKeyword.toString().toLowerCase());
    }

}
