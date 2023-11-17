import java.util.Map;
import java.util.Queue;
import java.util.Stack;

/**
 * There are 6 different types of "Statement".
 * <stmt> ==> <assign>
 * <stmt> ==> <if>
 * <stmt> ==> <loop>
 * <stmt> ==> <out>
 * <stmt> ==> <in>
 * <stmt> ==> <decl>
 * <stmt> ==> <call>
 * <p>
 * If the first word of tokenQueue is the keyword "if", then it is an "<if>" statement.
 * If the first word of tokenQueue is the keyword "while", then it is a "<loop>" statement.
 * If the first word of tokenQueue is the keyword "out", then it is an "<out>" statement.
 * If the first word of tokenQueue is the keyword "in", then it is an "<in>" statement.
 * If the first word of tokenQueue is the keyword "integer" or "array", then it is an "<decl>" statement.
 * If the first word of tokenQueue is the keyword "begin", then it is an "<call>" statement.
 * Otherwise, it is a "<assign>" statement.
 *
 * @author Zhao Liu
 */
public class Statement {
    private Assign assign;
    private If ifStmt;
    private Loop loop;
    private Out out;
    private In in;
    private Declaration declaration;
    private Call call;

    /**
     * The grammar is: <stmt> ==> <assign> | <if> | <loop> | <out> | <in> | <decl>
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue, Map<String, Function> functionMap) {
        Object firstWord = tokenQueue.peek();

        if (firstWord == Core.IF) {
            ifStmt = new If();
            ifStmt.parse(tokenQueue, functionMap);

        } else if (firstWord == Core.WHILE) {
            loop = new Loop();
            loop.parse(tokenQueue, functionMap);

        } else if (firstWord == Core.OUT) {
            out = new Out();
            out.parse(tokenQueue, functionMap);

        } else if (firstWord == Core.IN) {
            in = new In();
            in.parse(tokenQueue, functionMap);

        } else if (firstWord == Core.INTEGER || firstWord == Core.ARRAY) {
            declaration = new Declaration();
            declaration.parse(tokenQueue, functionMap);

        } else if (firstWord == Core.BEGIN) {
            call = new Call();
            call.parse(tokenQueue, functionMap);
        } else {
            assign = new Assign();
            assign.parse(tokenQueue, functionMap);
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
        if (assign != null) {
            assign.semanticChecking(variableStack, functionCheckingMap);

        } else if (ifStmt != null) {
            ifStmt.semanticChecking(variableStack, functionCheckingMap);

        } else if (loop != null) {
            loop.semanticChecking(variableStack, functionCheckingMap);

        } else if (out != null) {
            out.semanticChecking(variableStack, functionCheckingMap);

        } else if (in != null) {
            in.semanticChecking(variableStack, functionCheckingMap);

        } else if (call != null) {
            call.semanticChecking(variableStack, functionCheckingMap);
        } else {
            declaration.semanticChecking(variableStack, functionCheckingMap);
        }
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     */
    public void execute(Memory memory, Map<String, Function> functionMap) {
        if (assign != null) {
            assign.execute(memory, functionMap);

        } else if (ifStmt != null) {
            ifStmt.execute(memory, functionMap);

        } else if (loop != null) {
            loop.execute(memory, functionMap);

        } else if (out != null) {
            out.execute(memory, functionMap);

        } else if (in != null) {
            in.execute(memory, functionMap);

        } else if (call != null) {
            call.execute(memory, functionMap);
        } else {
            declaration.execute(memory, functionMap);
        }
    }


    /**
     * produce "pretty" code with the appropriate indentation
     *
     * @param indent the number of spaces which need to be print
     */
    public void print(int indent) {
        if (assign != null) {
            assign.print(indent);

        } else if (ifStmt != null) {
            ifStmt.print(indent);

        } else if (loop != null) {
            loop.print(indent);

        } else if (out != null) {
            out.print(indent);

        } else if (in != null) {
            in.print(indent);

        } else if (call != null) {
            call.print(indent);
        } else {
            declaration.print(indent);
        }
    }

}
