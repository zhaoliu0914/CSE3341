import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * There are 2 different types of "Declaration" operations available.
 * <decl> ==> <decl-integer>
 * <decl> ==> <decl-array>
 * <p>
 * If the first char is "Integer", it would be "<decl-integer>"
 * Otherwise, it is "<decl-array>"
 *
 * @author Zhao Liu
 */
public class Declaration {
    private DeclarationInteger declarationInteger;
    private DeclarationArray declarationArray;

    /**
     * The grammar is: <decl> ==> <decl-integer> | <decl-array>
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue, Map<String, Function> functionMap) {
        if (tokenQueue.peek() == Core.INTEGER) {
            declarationInteger = new DeclarationInteger();
            declarationInteger.parse(tokenQueue, functionMap);
        } else {
            declarationArray = new DeclarationArray();
            declarationArray.parse(tokenQueue, functionMap);
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
        if (declarationInteger != null) {
            declarationInteger.semanticChecking(variableStack, functionCheckingMap);
        } else {
            declarationArray.semanticChecking(variableStack, functionCheckingMap);
        }
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     */
    public void execute(Memory memory, Map<String, Function> functionMap) {
        if (declarationInteger != null) {
            declarationInteger.execute(memory, functionMap);
        } else {
            declarationArray.execute(memory, functionMap);
        }
    }


    /**
     * produce "pretty" code with the appropriate indentation
     *
     * @param indent the number of spaces which need to be print
     */
    public void print(int indent) {
        if (declarationInteger != null) {
            declarationInteger.print(indent);
        } else {
            declarationArray.print(indent);
        }
    }

}
