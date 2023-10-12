import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

class Main {
    public static void main(String[] args) {
        // Initialize the scanner with the input file
        // !!!! for test
        //java.util.Scanner input = new java.util.Scanner(System.in);
        //System.out.print("enter a program file:");
        //String fileName = input.nextLine();
        //Scanner programScanner = new Scanner(fileName);
        //java.util.Scanner input2 = new java.util.Scanner(System.in);
        //System.out.print("enter a data file:");
        //String dataFileName = input2.nextLine();
        //Scanner dataScanner = new Scanner(dataFileName);
        // !!!! for test

        Scanner programScanner = new Scanner(args[0]);
        Scanner dataScanner = new Scanner(args[1]);
        Queue<Object> tokenQueue = new LinkedList<>();
        Queue<Integer> inputDataQueue = new LinkedList<>();

        // Print the token stream
        while (programScanner.currentToken() != Core.EOS && programScanner.currentToken() != Core.ERROR) {
            // Pring the current token, with any extra data needed
            Core currentToken = programScanner.currentToken();
            tokenQueue.add(currentToken);
            //System.out.print(currentToken);
            if (programScanner.currentToken() == Core.ID) {
                String value = programScanner.getId();
                tokenQueue.add(value);
                //System.out.print("[" + value + "]");
            } else if (programScanner.currentToken() == Core.CONST) {
                int value = programScanner.getConst();
                tokenQueue.add(value);
                //System.out.print("[" + value + "]");
            }
            //System.out.print("\n");

            // Advance to the next token
            programScanner.nextToken();
        }

        // Read data from input file
        while (dataScanner.currentToken() != Core.EOS && dataScanner.currentToken() != Core.ERROR) {
            int value = dataScanner.getConst();
            inputDataQueue.add(value);

            dataScanner.nextToken();
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
        //procedure.print();

        //System.out.println("===================Execute the Program===================");
        Memory memory = Memory.getInstance();
        memory.setInputDataQueue(inputDataQueue);
        procedure.execute(memory);
    }
}