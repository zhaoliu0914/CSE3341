import java.util.*;

/**
 * There is only 1 types of "Parameter" operations available.
 * <call> ==> begin ID ( <parameters> ) ;
 *
 * @author Zhao Liu
 */
public class Call {

    private Core beginKeyword;
    private String functionName;
    private Parameter parameter;

    /**
     * The grammar is: <call> ::= begin ID ( <parameters> ) ;
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue, Map<String, Function> functionMap) {
        if (tokenQueue.poll() != Core.BEGIN) {
            System.out.println("ERROR: missing keyword 'begin'!!!");
            System.exit(1);
        }
        beginKeyword = Core.BEGIN;

        tokenQueue.poll();
        functionName = (String) tokenQueue.poll();

        if (tokenQueue.poll() != Core.LPAREN) {
            System.out.println("ERROR: missing symbol '('!!!");
            System.exit(1);
        }

        if (tokenQueue.peek() == Core.RPAREN) {
            System.out.println("ERROR: Call function " + functionName + " is missing arguments!!!");
            System.exit(1);
        }

        parameter = new Parameter();
        parameter.parse(tokenQueue, functionMap);

        if (tokenQueue.poll() != Core.RPAREN) {
            System.out.println("ERROR: missing symbol ')'!!!");
            System.exit(1);
        }

        if (tokenQueue.poll() != Core.SEMICOLON) {
            System.out.println("ERROR: missing semicolon symbol ';'");
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
    public void semanticChecking(Stack<Variable> variableStack, Map<String, Function> functionCheckingMap) {
        boolean isExist = false;
        for (String existFunctionName : functionCheckingMap.keySet()) {
            if (existFunctionName.equals(functionName)) {
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            System.out.println("ERROR: Function call has an invalid target. Function " + functionName + " does not exist!!!");
            System.exit(1);
        }
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     */
    public void execute(Memory memory, Map<String, Function> functionMap) {
        Queue<String> argumentQueue = new LinkedList<>();
        parameter.execute(memory, argumentQueue, functionMap, true);

        // Indicate running/executing a function now.
        memory.setExecutingFunction(true);
        // Push/create a new call stack / variables stack into memory.
        memory.pushNewVariableStack();

        Function function = functionMap.get(functionName);
        // Execute a Function
        function.execute(memory, argumentQueue, functionMap);

        // Indicate finished a function executing
        memory.setExecutingFunction(false);
        // Pop/remove this function variable stack from Call stack.
        memory.popVariableStack();
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

        System.out.print(beginKeyword.toString().toLowerCase() + " ");
        System.out.print(functionName);
        System.out.print("(");
        if (parameter != null) {
            parameter.print(0);
        }
        System.out.print(")");
        System.out.println(";");

    }
}
