import java.util.Queue;
import java.util.Stack;

/**
 * There are 4 different types of "Assign" operations available.
 * <assign> ==> id := <expr> ;
 * <assign> ==> id [ <expr> ] := <expr> ;
 * <assign> ==> id := new integer [ <expr> ];
 * <assign> ==> id := array id ;
 * <p>
 * If the type of variable on the Left-hand Side is Array, then there is only one matched: "id [ <expr> ] := <expr> ;"
 * So the Right-hand Side has to be "<expr>". Thus, variable isLHSArray=true indicates this situation.
 * <p>
 * If the type of variable on the Left-hand Side is Integer, then it will match: "id := <expr> ;" or "id := new integer [ <expr> ];" or "id := array id ;"
 * Since there are 3 cases will match the situation, we have to analyse case by case.
 * Case 1: Right-hand side is "id := new integer [ <expr> ];"
 * Then isRHSNewInteger=true
 * Case 2: Right-hand side is "id := array id ;"
 * Then isRHSNewArray=true
 * Case 3: "id := <expr> ;" will be only case.
 *
 * @author Zhao Liu
 */
public class Assign {
    // This 3 variables indicate what is on the Left-Hand Side of the equal sign (=) and what is on Right-Hand Side of the equal sign (=)
    private boolean isLHSArray;
    private boolean isRHSNewInteger;
    private boolean isRHSNewArray;

    // Left-hand side
    private String lhsVariable;
    private Core lhsLeftBracket;
    private Core lhsRightBracket;
    private Expression lhsExpression;

    // assign symbol
    private Core assign;

    // right-hand side
    private String rhsVariable;
    private Expression rhsExpression;
    private Core rhsNewKeyword;
    private Core rhsIntegerKeyword;
    private Core rhsArrayKeyword;
    private Core rhsLeftBracket;
    private Core rhsRightBracket;
    private Core semicolon;

    /**
     * The grammar is: <assign> ==> id := <expr> ; | id [ <expr> ] := <expr> ; | id := new integer [ <expr> ]; | id := array id ;
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue) {
        if (tokenQueue.poll() != Core.ID) {
            System.out.println("ERROR: missing identifier or variable.");
            System.exit(1);
        }
        lhsVariable = (String) tokenQueue.poll();

        // If send token is "[", then the type of variable on the left-hand side variable is "array".
        // Then, isLHSArray=true, the statement "id [ <expr> ] := <expr> ;" is the only accepted input.
        if (tokenQueue.peek() == Core.LBRACE) {
            isLHSArray = true;

            if (tokenQueue.poll() != Core.LBRACE) {
                System.out.println("ERROR: missing left bracket '[' for the variable in the equation " + lhsVariable);
                System.exit(1);
            }
            lhsLeftBracket = Core.LBRACE;

            lhsExpression = new Expression();
            lhsExpression.parse(tokenQueue);

            if (tokenQueue.poll() != Core.RBRACE) {
                System.out.println("ERROR: missing right bracket ']' for the variable in the equation " + lhsVariable);
                System.exit(1);
            }
            lhsRightBracket = Core.RBRACE;

            if (tokenQueue.poll() != Core.ASSIGN) {
                System.out.println("ERROR: missing assign symbol ':=' for the variable in the equation " + lhsVariable);
                System.exit(1);
            }
            assign = Core.ASSIGN;

            rhsExpression = new Expression();
            rhsExpression.parse(tokenQueue);

        } else {
            if (tokenQueue.poll() != Core.ASSIGN) {
                System.out.println("ERROR: missing assign symbol ':=' for the variable in the equation " + lhsVariable);
                System.exit(1);
            }
            assign = Core.ASSIGN;

            if (tokenQueue.peek() == Core.NEW) {
                // Case 1: Right-hand side is "id := new integer [ <expr> ];"
                // Then isRHSNewInteger=true
                isRHSNewInteger = true;

                tokenQueue.poll();
                rhsNewKeyword = Core.NEW;

                if (tokenQueue.poll() != Core.INTEGER) {
                    System.out.println("ERROR: missing keyword 'integer' for the variable in the equation " + lhsVariable);
                    System.exit(1);
                }
                rhsIntegerKeyword = Core.INTEGER;

                if (tokenQueue.poll() != Core.LBRACE) {
                    System.out.println("ERROR: missing left bracket '[' for the variable in the equation " + lhsVariable);
                    System.exit(1);
                }
                rhsLeftBracket = Core.LBRACE;

                rhsExpression = new Expression();
                rhsExpression.parse(tokenQueue);

                if (tokenQueue.poll() != Core.RBRACE) {
                    System.out.println("ERROR: missing right bracket ']' for the variable in the equation " + lhsVariable);
                    System.exit(1);
                }
                rhsRightBracket = Core.RBRACE;

            } else if (tokenQueue.peek() == Core.ARRAY) {
                // Case 2: Right-hand side is "id := array id ;"
                // Then isRHSNewArray=true
                isRHSNewArray = true;

                tokenQueue.poll();
                rhsArrayKeyword = Core.ARRAY;

                rhsVariable = (String) tokenQueue.poll();
            } else {
                // Case 3: "id := <expr> ;" will be only case.

                rhsExpression = new Expression();
                rhsExpression.parse(tokenQueue);
            }
        }

        if (tokenQueue.poll() != Core.SEMICOLON) {
            System.out.println("ERROR: missing semicolon symbol ';' for the variable in the equation " + lhsVariable);
            System.exit(1);
        }
        semicolon = Core.SEMICOLON;
    }

    /**
     * It will perform a Semantic Checking.
     * There are several types of Semantic Errors need to be checked.
     * 1. Undeclared variables or out of scope.
     * 2. Doubly-declared variables.
     * 3. variable type (integer or array) has to be matched when performing an assign operation.
     * <p>
     * This class Assign may occur assigning value to undeclared variable or unmatched variable type.
     *
     * @param variableStack contains all declared variables
     */
    public void semanticChecking(Stack<Variable> variableStack) {
        boolean isLHSVariableExist = false;
        Variable lhsVariableAttribute = null;
        for (Variable temp : variableStack) {
            if (temp.getName().equals(lhsVariable)) {
                isLHSVariableExist = true;
                lhsVariableAttribute = temp;
                break;
            }
        }
        if (!isLHSVariableExist) {
            System.out.println("ERROR: the variable " + lhsVariable + " has not been declared before!!!");
            System.exit(1);
        }

        Core lhsType = lhsVariableAttribute.getType();

        if (lhsType == Core.INTEGER && isRHSNewInteger) {
            System.out.println("ERROR: the variable " + lhsVariable + " is Integer type but assigned Array type to it, which is not available!!!");
            System.exit(1);
        }
        if (lhsType == Core.INTEGER && isRHSNewArray) {
            System.out.println("ERROR: the variable " + lhsVariable + " is Integer type but assigned Array type to it, which is not available!!!");
            System.exit(1);
        }
        if (lhsType == Core.INTEGER && isLHSArray) {
            System.out.println("ERROR: the variable " + lhsVariable + " has to be Array type to use symbols '[]'!!!");
            System.exit(1);
        }
        /*
        if (lhsType == Core.ARRAY && !isLHSArray) {
            System.out.println("ERROR: the variable " + lhsVariable + " is Array type, can not assign an integer or expression to it!!!");
            System.exit(1);
        }
        */

        if (lhsExpression != null) {
            lhsExpression.semanticChecking(variableStack);
        }
        if (rhsExpression != null) {
            rhsExpression.semanticChecking(variableStack);
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

        System.out.print(lhsVariable);

        if (isLHSArray) {
            System.out.print("[");
            lhsExpression.print(0);
            System.out.print("]");
            System.out.print(" := ");
            rhsExpression.print(0);

        } else if (isRHSNewInteger) {
            System.out.print(" := ");
            System.out.print(rhsNewKeyword.toString().toLowerCase() + " ");
            System.out.print(rhsIntegerKeyword.toString().toLowerCase());
            System.out.print("[");
            rhsExpression.print(0);
            System.out.print("]");

        } else if (isRHSNewArray) {
            System.out.print(" := ");
            System.out.print(rhsArrayKeyword.toString().toLowerCase() + " ");
            System.out.print(rhsVariable);

        } else {
            System.out.print(" := ");
            rhsExpression.print(0);
        }

        System.out.println(";");
    }

}
