import java.util.Queue;
import java.util.Stack;

/**
 * There are 4 different types of "Factor" operations available.
 * <factor> ==> id
 * <factor> ==> id [ <expr> ]
 * <factor> ==> const
 * <factor> ==> ( <expr> )
 * <p>
 * If the first token is "ID", then it will match "id" or "id [ <expr> ]".
 * Base on this case, if the third token is symbol "[", then it will match "id [ <expr> ]"
 * <p>
 * If the first token is "const", then it only matches "const"
 * If the first token is symbol "(", then it only matches "( <expr> )"
 *
 * @author Zhao Liu
 */
public class Factor {
    private String variable;
    private String constant;
    private Core leftBracket;
    private Core rightBracket;
    private Core leftParenthesis;
    private Core rightParenthesis;
    private Expression expression;

    /**
     * The grammar is: <factor> ==> id | id [ <expr> ] | const | ( <expr> )
     *
     * @param tokenQueue a sequence of tokens as input to the parser.
     */
    public void parse(Queue<Object> tokenQueue) {
        if (tokenQueue.peek() == Core.ID) {
            // If the first token is "ID", then it will match "id" or "id [ <expr> ]".
            tokenQueue.poll();
            variable = (String) tokenQueue.poll();

            if (tokenQueue.peek() == Core.LBRACE) {
                // if the third token is symbol "[", then it will match "id [ <expr> ]"
                tokenQueue.poll();
                leftBracket = Core.LBRACE;

                expression = new Expression();
                expression.parse(tokenQueue);

                if (tokenQueue.poll() != Core.RBRACE) {
                    System.out.println("ERROR: missing symbol ']'!!!");
                    System.exit(1);
                }
                rightBracket = Core.RBRACE;
            }

        } else if (tokenQueue.peek() == Core.CONST) {
            // If the first token is "const", then it only matches "const"
            tokenQueue.poll();
            constant = String.valueOf(tokenQueue.poll());

        } else if (tokenQueue.peek() == Core.LPAREN) {
            // If the first token is symbol "(", then it only matches "( <expr> )"
            tokenQueue.poll();
            leftParenthesis = Core.LPAREN;

            expression = new Expression();
            expression.parse(tokenQueue);

            if (tokenQueue.poll() != Core.RPAREN) {
                System.out.println("ERROR: missing symbol ')'!!!");
                System.exit(1);
            }
            rightParenthesis = Core.RPAREN;

        } else {
            System.out.println("ERROR: There are some unacceptable chars in the '<factor>' statement!!!");
            System.exit(1);
        }
    }

    /**
     * It will perform a Semantic Checking.
     * There are several types of Semantic Errors need to be checked.
     * 1. Undeclared variables or out of scope.
     * 2. Doubly-declared variables.
     * 3. variable type (integer or array) has to be matched when performing an assign operation.
     * <p>
     * This class Factor may occur undeclared variable or Integer type with symbol "[]"
     *
     * @param variableStack contains all declared variables
     */
    public void semanticChecking(Stack<Variable> variableStack) {
        if (constant != null || leftParenthesis != null) {
            return;
        }

        boolean isVariableExist = false;
        Variable variableAttribute = null;
        for (Variable temp : variableStack) {
            if (temp.getName().equals(variable)) {
                isVariableExist = true;
                variableAttribute = temp;
                break;
            }
        }
        if (!isVariableExist) {
            System.out.println("ERROR: the variable " + variable + " has not been declared before!!!");
            System.exit(1);
        }

        if (variableAttribute.getType() == Core.INTEGER && leftBracket != null) {
            System.out.println("ERROR: the variable " + variable + " has to be Array type to use symbol '[]'!!!");
            System.exit(1);
        }

        if (expression != null) {
            expression.semanticChecking(variableStack);
        }
    }

    /**
     * produce "pretty" code with the appropriate indentation
     *
     * @param indent the number of spaces which need to be print
     */
    public void print(int indent) {
        if (variable != null) {
            System.out.print(variable);
            if (leftBracket != null) {
                System.out.print("[");
                expression.print(0);
                System.out.println("]");
            }

        } else if (constant != null) {
            System.out.print(constant);
        } else {
            System.out.print("(");
            expression.print(0);
            System.out.print(")");
        }

    }

}
