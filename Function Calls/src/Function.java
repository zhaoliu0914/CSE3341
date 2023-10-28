import java.util.*;

/**
 * The definition of "<function>" is "procedure ID ( <parameters> ) is <stmt-seq> end"
 * <function> ==> procedure ID ( <parameters> ) is <stmt-seq> end
 * <p>
 * If the first char from the token queue is "begin", it indicates the end of all "Declaration" statement.
 * If the first char from the token queue is "procedure", it indicates "<function>"
 *
 * @author Zhao Liu
 */
public class Function {

    private Core functionKeyword;
    private String functionName;
    private Parameter parameter;
    private Core isKeyword;
    private StatementSequence statementSequence;
    private Core endKeyword;

    /**
     * The grammar is: <function> ::= procedure ID ( <parameters> ) is <stmt-seq> end
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue, Map<String, Function> functionMap) {
        if (tokenQueue.poll() != Core.PROCEDURE) {
            System.out.println("ERROR: missing keyword 'procedure'!!!");
            System.exit(1);
        }
        functionKeyword = Core.PROCEDURE;

        if (tokenQueue.poll() != Core.ID) {
            System.out.println("ERROR: missing procedure name!!!");
            System.exit(1);
        }
        functionName = String.valueOf(tokenQueue.poll());

        functionMap.put(functionName, null);

        if (tokenQueue.poll() != Core.LPAREN) {
            System.out.println("ERROR: missing symbol '('!!!");
            System.exit(1);
        }

        if (tokenQueue.peek() == Core.RPAREN) {
            System.out.println("ERROR: function " + functionName + " is missing formal parameter!!!");
            System.exit(1);
        }

        parameter = new Parameter();
        parameter.parse(tokenQueue, functionMap);

        if (tokenQueue.poll() != Core.RPAREN) {
            System.out.println("ERROR: missing symbol ')'!!!");
            System.exit(1);
        }

        if (tokenQueue.poll() != Core.IS) {
            System.out.println("ERROR: missing keyword 'is'!!!");
            System.exit(1);
        }
        isKeyword = Core.IS;

        if (tokenQueue.peek() == Core.END) {
            System.out.println("ERROR: function " + functionName + " is missing method body. There is no <stmt-seq>!!!");
            System.exit(1);
        }

        statementSequence = new StatementSequence();
        statementSequence.parse(tokenQueue, functionMap);

        if (tokenQueue.poll() != Core.END) {
            System.out.println("ERROR: missing keyword 'end'!!!");
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
        for (String existFunctionName : functionCheckingMap.keySet()) {
            if (existFunctionName.equals(functionName)) {
                System.out.println("ERROR: function should have a unique name. Function name " + functionName + " has been used!!!");
                System.exit(1);
            }
        }

        functionCheckingMap.put(functionName, this);

        if (parameter != null) {
            parameter.semanticChecking(variableStack, functionCheckingMap);
        }

        if (statementSequence != null) {
            statementSequence.semanticChecking(variableStack, functionCheckingMap);
        }
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     */
    public void execute(Memory memory, Queue<String> argumentQueue, Map<String, Function> functionMap) {
        parameter.execute(memory, argumentQueue, functionMap, false);

        statementSequence.execute(memory,functionMap);
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

        System.out.print(functionKeyword.toString().toLowerCase() + " ");
        System.out.print(functionName);
        System.out.print("(");
        if (parameter != null) {
            parameter.print(0);
        }
        System.out.print(")");
        System.out.print(" ");
        System.out.println(isKeyword.toString().toLowerCase());

        if (statementSequence != null) {
            statementSequence.print(indent + 4);
        }

        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        System.out.println(endKeyword.toString().toLowerCase());
    }

    public String getFunctionName() {
        return functionName;
    }
}
