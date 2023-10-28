import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * There are 4 different types of "DeclarationSequence" operations available.
 * <decl-seq> ==> <decl>
 * <decl-seq> ==> <decl><decl-seq>
 * <decl-seq> ==> <function>
 * <decl-seq> ==> <function><decl-seq>
 * <p>
 * If the first char from the token queue is "begin", it indicates the end of all "Declaration" statement.
 * If the first char from the token queue is "procedure", it indicates "<function>"
 *
 * @author Zhao Liu
 */
public class DeclarationSequence {

    private Declaration declaration;
    private Function function;
    private DeclarationSequence declarationSequence;

    /**
     * The grammar is: <decl-seq> ::= <decl > | <decl><decl-seq> | <function> | <function><decl-seq>
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue, Map<String, Function> functionMap) {
        if (tokenQueue.peek() == Core.PROCEDURE) {
            function = new Function();
            function.parse(tokenQueue, functionMap);

            String functionName = function.getFunctionName();
            functionMap.put(functionName, function);

        } else {
            declaration = new Declaration();
            declaration.parse(tokenQueue, functionMap);
        }

        if (tokenQueue.peek() != Core.BEGIN) {
            declarationSequence = new DeclarationSequence();
            declarationSequence.parse(tokenQueue, functionMap);
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
        if (function != null) {
            int initialSize = variableStack.size();

            function.semanticChecking(variableStack, functionCheckingMap);

            // clean all variables in "function" statement from Stack.
            while (variableStack.size() > initialSize) {
                variableStack.pop();
            }
        } else {
            declaration.semanticChecking(variableStack, functionCheckingMap);
        }

        if (declarationSequence != null) {
            declarationSequence.semanticChecking(variableStack, functionCheckingMap);
        }
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     */
    public void execute(Memory memory, Map<String, Function> functionMap) {
        memory.setDeclSeqFinished(false);

        if (declaration != null) {
            declaration.execute(memory, functionMap);
        }
        if (declarationSequence != null) {
            declarationSequence.execute(memory, functionMap);
        }

        memory.setDeclSeqFinished(true);
    }


    /**
     * produce "pretty" code with the appropriate indentation
     *
     * @param indent the number of spaces which need to be print
     */
    public void print(int indent) {
        if (function != null) {
            function.print(indent);
        } else {
            declaration.print(indent);
        }

        if (declarationSequence != null) {
            declarationSequence.print(indent);
        }
    }
}
