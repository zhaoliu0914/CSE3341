import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

class Main {
    public static void main(String[] args) {
        // Initialize the scanner with the input file
        // !!!! for test
        //java.util.Scanner input = new java.util.Scanner(System.in);
        //System.out.print("enter a file:");
        //String fileName = input.nextLine();
        //Scanner S = new Scanner(fileName);
        // !!!! for test

        Scanner S = new Scanner(args[0]);
        Queue<Object> tokenQueue = new LinkedList<>();

        // Print the token stream
        while (S.currentToken() != Core.EOS && S.currentToken() != Core.ERROR) {
            // Pring the current token, with any extra data needed
            Core currentToken = S.currentToken();
            tokenQueue.add(currentToken);
            System.out.print(currentToken);
            if (S.currentToken() == Core.ID) {
                String value = S.getId();
                tokenQueue.add(value);
                System.out.print("[" + value + "]");
            } else if (S.currentToken() == Core.CONST) {
                int value = S.getConst();
                tokenQueue.add(value);
                System.out.print("[" + value + "]");
            }
            System.out.print("\n");

            // Advance to the next token
            S.nextToken();
        }

        //System.out.println("===================Print Token Queue===================");
        //System.out.println(tokenQueue);

        //System.out.println("===================Parser===================");
        Procedure procedure = new Procedure();
        procedure.parse(tokenQueue);

        //System.out.println("===================Semantic Check===================");
        Stack<Variable> variableStack = new Stack<>();
        procedure.semanticChecking(variableStack);

        //System.out.println("===================Print Parser Tree===================");
        procedure.print();
    }
}