import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * There are 2 different types of "Parameter" operations available.
 * <parameters> ==> ID
 * <parameters> ==> ID , <parameters>
 * <p>
 *
 * @author Zhao Liu
 */
public class Parameter {

    private String name;
    private Parameter parameter;

    /**
     * The grammar is: <parameters> ::= ID | ID , <parameters>
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue, Map<String, Function> functionMap) {
        tokenQueue.poll();
        name = (String) tokenQueue.poll();

        if (tokenQueue.peek() == Core.COMMA) {
            tokenQueue.poll();

            parameter = new Parameter();
            parameter.parse(tokenQueue, functionMap);
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
        for (Variable temp : variableStack) {
            if (temp.getName().equals(name)) {
                System.out.println("ERROR: The formal parameters should be distinct from each other. Formal parameter " + name + " has been used!!!");
                System.exit(1);
            }
        }

        Variable attribute = new Variable();
        attribute.setName(name);
        variableStack.push(attribute);

        if (parameter != null) {
            parameter.semanticChecking(variableStack, functionCheckingMap);
        }
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     */
    public void execute(Memory memory, Queue<String> argumentQueue, Map<String, Function> functionMap, boolean isLookupArgument) {
        if (isLookupArgument) {
            argumentQueue.add(name);
        } else {
            // parameter passing will be done with call by sharing

            String argument = argumentQueue.poll();

            // create a new local variable for formal parameter
            memory.allocate(Core.ARRAY, name);

            // Copy the reference of argument to formal parameter
            memory.copyBySharing(name, argument);
        }

        if (parameter != null) {
            parameter.execute(memory, argumentQueue, functionMap, isLookupArgument);
        }
    }


    /**
     * produce "pretty" code with the appropriate indentation
     *
     * @param indent the number of spaces which need to be print
     */
    public void print(int indent) {
        System.out.print(name);
        if (parameter != null) {
            System.out.print(", ");
            parameter.print(0);
        }
    }
}
