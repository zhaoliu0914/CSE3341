import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * There is only 1 case available.
 * <loop> ==> while <cond> do <stmt-seq> end
 * <p>
 * This case is quiet simple. We only need to check whether missing chars from the statement.
 *
 * @author Zhao Liu
 */
public class Loop {
    private Core whileKeyword;
    private Condition condition;
    private Core doKeyword;
    private StatementSequence statementSequence;
    private Core endKeyword;

    /**
     * The grammar is: <loop> ==> while <cond> do <stmt-seq> end
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue, Map<String, Function> functionMap) {
        if (tokenQueue.poll() != Core.WHILE) {
            System.out.println("ERROR: missing keyword 'while' for while statement!!!");
            System.exit(1);
        }
        whileKeyword = Core.WHILE;

        condition = new Condition();
        condition.parse(tokenQueue, functionMap);

        if (tokenQueue.poll() != Core.DO) {
            System.out.println("ERROR: missing keyword 'do' for while statement!!!");
            System.exit(1);
        }
        doKeyword = Core.DO;

        statementSequence = new StatementSequence();
        statementSequence.parse(tokenQueue, functionMap);

        if (tokenQueue.poll() != Core.END) {
            System.out.println("ERROR: missing keyword 'end' for while statement!!!");
            System.exit(1);
        }
        endKeyword = Core.END;
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
        int initialSize = variableStack.size();

        condition.semanticChecking(variableStack, functionCheckingMap);
        statementSequence.semanticChecking(variableStack, functionCheckingMap);

        // clean all variables in "while" statement from Stack.
        while (variableStack.size() > initialSize) {
            variableStack.pop();
        }
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     * <p>
     * There is only one case, "<loop> ::= while <cond> do <stmt-seq> end"
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     */
    public void execute(Memory memory, Map<String, Function> functionMap) {
        int initialSize = memory.localSize();

        boolean conditionValue = condition.execute(memory, functionMap);
        // repeat run "<stmt-seq>", if the result of "<cond>" is true.
        while (conditionValue) {
            statementSequence.execute(memory, functionMap);

            while (memory.localSize() > initialSize) {
                memory.popLocalElement();
            }

            conditionValue = condition.execute(memory, functionMap);
        }

        while (memory.localSize() > initialSize) {
            memory.popLocalElement();
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

        System.out.print(whileKeyword.toString().toLowerCase() + " ");
        condition.print(0);
        System.out.println(" " + doKeyword.toString().toLowerCase());

        int statementIndent = indent + 4;
        statementSequence.print(statementIndent);

        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        System.out.println(endKeyword.toString().toLowerCase());

    }

}
