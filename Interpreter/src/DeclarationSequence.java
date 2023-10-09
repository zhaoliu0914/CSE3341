import java.util.Queue;
import java.util.Stack;

/**
 * There are 2 different types of "DeclarationSequence" operations available.
 * <decl-seq> ==> <decl>
 * <decl-seq> ==> <decl><decl-seq>
 * <p>
 * If the first char from the token queue is "begin", it indicates the end of all "Declaration" statement.
 *
 * @author Zhao Liu
 */
public class DeclarationSequence {

    private Declaration declaration;
    private DeclarationSequence declarationSequence;

    /**
     * The grammar is: <decl-seq> ==> <decl > | <decl><decl-seq>
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue) {
        declaration = new Declaration();
        declaration.parse(tokenQueue);

        if (tokenQueue.peek() != Core.BEGIN) {
            declarationSequence = new DeclarationSequence();
            declarationSequence.parse(tokenQueue);
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
        declaration.semanticChecking(variableStack);

        if (declarationSequence != null) {
            declarationSequence.semanticChecking(variableStack);
        }
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     */
    public void execute(Memory memory) {
        memory.setDeclSeqFinished(false);

        declaration.execute(memory);
        if (declarationSequence != null) {
            declarationSequence.execute(memory);
        }

        memory.setDeclSeqFinished(true);
    }


    /**
     * produce "pretty" code with the appropriate indentation
     *
     * @param indent the number of spaces which need to be print
     */
    public void print(int indent) {
        declaration.print(indent);

        if (declarationSequence != null) {
            declarationSequence.print(indent);
        }
    }
}
