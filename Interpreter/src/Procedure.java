import java.util.Locale;
import java.util.Queue;
import java.util.Stack;

/**
 * This is the class that represents parser tree.
 * Every method like "parse()", "semanticChecking()", and "print()" will invoke this class first.
 * <p>
 * There are 2 different types of "Procedure" operations available.
 * <procedure> ==> procedure ID is <decl-seq> begin <stmt-seq> end
 * <procedure> ==> procedure ID is begin <stmt-seq> end
 * <p>
 * This only difference is whether containing "<decl-seq>".
 * If it is containing "<decl-seq>", variable declarationSequence will be assigned value.
 *
 * @author Zhao Liu
 */
public class Procedure {
    private Core procedureKeyword;
    private String procedureName;
    private Core isKeyword;
    private DeclarationSequence declarationSequence;
    private Core beginKeyword;
    private StatementSequence statementSequence;
    private Core endKeyword;

    /**
     * This is main entry to generate a parse tree.
     * The grammar is: <procedure> ==> procedure ID is <decl-seq> begin <stmt-seq> end | procedure ID is begin <stmt-seq> end
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue) {
        if (tokenQueue.poll() != Core.PROCEDURE) {
            System.out.println("ERROR: missing keyword 'procedure'!!!");
            System.exit(1);
        }
        procedureKeyword = Core.PROCEDURE;

        if (tokenQueue.poll() != Core.ID) {
            System.out.println("ERROR: missing procedure name!!!");
            System.exit(1);
        }
        procedureName = String.valueOf(tokenQueue.poll());

        if (tokenQueue.poll() != Core.IS) {
            System.out.println("ERROR: missing keyword 'is'!!!");
            System.exit(1);
        }
        isKeyword = Core.IS;

        if (tokenQueue.peek() == Core.BEGIN) {
            tokenQueue.poll();
        } else {
            declarationSequence = new DeclarationSequence();
            declarationSequence.parse(tokenQueue);

            if (tokenQueue.poll() != Core.BEGIN) {
                System.out.println("ERROR: missing keyword 'begin'!!!");
                System.exit(1);
            }
        }
        beginKeyword = Core.BEGIN;

        boolean hasBody = true;
        if (tokenQueue.peek() == Core.END) {
            hasBody = false;
        }
        if (!hasBody) {
            System.out.println("The body can not be empty. There needs to be at lest one statement!!!");
            System.exit(1);
        }

        statementSequence = new StatementSequence();
        statementSequence.parse(tokenQueue);

        if (tokenQueue.poll() != Core.END) {
            System.out.println("ERROR: missing keyword 'end'!!!");
            System.exit(1);
        }
        endKeyword = Core.END;

        if (!tokenQueue.isEmpty()) {
            System.out.println("ERROR: There should not be any letters or chars after EOF keyword 'end' !!!");
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
        if (declarationSequence != null) {
            declarationSequence.semanticChecking(variableStack);
        }

        if (statementSequence != null) {
            statementSequence.semanticChecking(variableStack);
        }
    }

    /**
     * Using the recursive descent approach to walk over the parse tree.
     * This function will execute its children and perform any action needed on the result of that execution.
     *
     * @param memory simulating memory (Stack and Heap) for local and global variables
     */
    public void execute(Memory memory) {
        if (declarationSequence != null) {
            declarationSequence.execute(memory);
        }

        statementSequence.execute(memory);
    }

    /**
     * produce "pretty" code with the appropriate indentation
     */
    public void print() {
        int indent = 0;

        System.out.print(procedureKeyword.toString().toLowerCase() + " ");
        System.out.print(procedureName + " ");
        System.out.println(isKeyword.toString().toLowerCase());

        if (declarationSequence != null) {
            declarationSequence.print(indent + 4);
        }

        System.out.println(beginKeyword.toString().toLowerCase());

        if (statementSequence != null) {
            statementSequence.print(indent + 4);
        }

        System.out.println(endKeyword.toString().toLowerCase());
    }

}
