import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * The class implements the main function of the Scanning program code.
 * It's responsible to find the token (method nextToken()) and return the token the scanner is currently on (method currentToken()).
 *
 * @author Zhao Liu
 *
 */
class Scanner {
    private Core token;
    private StringBuilder id;
    private StringBuilder constants;

    private int currentUnicode = -22;
    private int nextUnicode;
    FileReader fileReader = null;
    BufferedReader bufferedReader = null;

    private final int ASCII_SPACE = 32;
    private final int ASCII_TAB = 9;
    private final int ASCII_RETURN_N = 10;
    private final int ASCII_RETURN_R = 13;
    private final int ASCII_UPPER_A = 65;
    private final int ASCII_UPPER_Z = 90;
    private final int ASCII_LOWER_A = 97;
    private final int ASCII_LOWER_Z = 122;
    private final int ASCII_0 = 48;
    private final int ASCII_9 = 57;
    private final int MAX_CONST = 100003;

    // Initialize the scanner
    Scanner(String filename) {
        try {
            // initializing the FileReader and BufferedReader
            fileReader = new FileReader(filename);
            bufferedReader = new BufferedReader(fileReader);

            // call nextToken() method.
            this.nextToken();
        } catch (FileNotFoundException e) {
            // Set token = Error
            token = Core.ERROR;
            e.printStackTrace();
            System.out.print("ERROR: " + filename + " does not exist!!!");

            System.exit(1);
        }
    }

    // Advance to the next token

    /**
     * Find the next token and waiting for method currentToken() to return it.
     */
    public void nextToken() {
        try {
            // if currentUnicode<-1 means that we have not call any read() method yet, we need to call read() method first.
            // if currentUnicode==-1 mean that we have reached the end of the file, we need to set token=EOS.
            if (currentUnicode < -1) {
                currentUnicode = bufferedReader.read();
            } else if (currentUnicode == -1) {
                endOfFile();
                return;
            }

            // convert UNICODE to ASCII
            int asciiCode = convertUnicodeToAscii(currentUnicode);

            // if char of current read is space,
            // continue read next char until find meaningful char, like letter, number, or symbol.
            while (asciiCode == ASCII_SPACE || asciiCode == ASCII_RETURN_N || asciiCode == ASCII_RETURN_R || asciiCode == ASCII_TAB) {
                currentUnicode = bufferedReader.read();

                // Check whether I/O steam reach the end of file.
                if (currentUnicode == -1) {
                    endOfFile();
                    return;
                }

                asciiCode = convertUnicodeToAscii(currentUnicode);
            }

            // determine whether the current read char is a letter, number or symbol
            if ((asciiCode >= ASCII_UPPER_A && asciiCode <= ASCII_UPPER_Z) || (asciiCode >= ASCII_LOWER_A && asciiCode <= ASCII_LOWER_Z)) {
                determineKeywordOrIdentity((char) currentUnicode);
            } else if (asciiCode >= ASCII_0 && asciiCode <= ASCII_9) {
                determineConstants((char) currentUnicode);
            } else {
                determineSymbols((char) currentUnicode);
            }

            currentUnicode = nextUnicode;
        } catch (IOException e) {
            // Set token = Error
            token = Core.ERROR;

            e.printStackTrace();
            System.out.println("ERROR: The char " + Character.toString(currentUnicode) + " is not legal one!!!");

            System.exit(1);
        }
    }

    /**
     * Convert Unicode to ASCII code
     *
     * @param unicode convert this {@code unicode} to ASCII code
     * @return integer which represent ASCII code according ASCII table.
     */
    private int convertUnicodeToAscii(int unicode) {
        // convert UNICODE to ASCII
        char currentChar = (char) unicode;
        byte[] asciiBytes = String.valueOf(currentChar).getBytes(StandardCharsets.US_ASCII);
        int asciiCode = asciiBytes[0];

        return asciiCode;
    }

    /**
     * This method will determine whether input value {@code currentChar} is a keyword or an Identity.
     * It will take the greedy approach to find sequence of chars until occurs any other Symbol like space or semicolon.
     * For example, 'whilewhile' and 'while123' are both produce ID token.
     * while 'while while' will produce Keywords [while] [while], 'while 123' will produce Keywords [while] Constants [123]
     * <p>
     * If it's a keyword, the global variable token = {@code keyword}.
     * If it's a Identity, the global variable token = Core.ID and id = {the str of Identity}
     *
     * @param currentChar the first char of a sequence of string.
     */
    private void determineKeywordOrIdentity(char currentChar) throws IOException {
        StringBuilder tempStr = new StringBuilder();
        tempStr.append(currentChar);

        boolean isContinue = true;
        do {
            nextUnicode = bufferedReader.read();
            int asciiCode = convertUnicodeToAscii(nextUnicode);

            // continue adding letters and number to tempStr.
            // if find any symbol, stop finding and return.
            if ((asciiCode >= ASCII_UPPER_A && asciiCode <= ASCII_UPPER_Z)
                    || (asciiCode >= ASCII_LOWER_A && asciiCode <= ASCII_LOWER_Z)
                    || (asciiCode >= ASCII_0 && asciiCode <= ASCII_9)) {
                char nextChar = (char) nextUnicode;
                tempStr.append(nextChar);
            } else {
                isContinue = false;
            }
        } while (isContinue);

        // Checking whether tempStr is a keyword or a ID.
        Core keyword = findKeyword(tempStr);
        if (keyword == null) {
            token = Core.ID;
            id = new StringBuilder(tempStr);
        } else {
            token = keyword;
        }
    }

    /**
     * Determine whether input string {@code tempStr} is a Keyword
     *
     * @param tempStr need to be determined
     * @return keyword, if {@code keyword} is null, then it's an Identity. If {@code keyword} is not null, then it's a Keyword.
     */
    private Core findKeyword(StringBuilder tempStr) {
        Core keyword;
        switch (tempStr.toString()) {
            case "procedure":
                keyword = Core.PROCEDURE;
                break;
            case "begin":
                keyword = Core.BEGIN;
                break;
            case "is":
                keyword = Core.IS;
                break;
            case "end":
                keyword = Core.END;
                break;
            case "if":
                keyword = Core.IF;
                break;
            case "else":
                keyword = Core.ELSE;
                break;
            case "in":
                keyword = Core.IN;
                break;
            case "integer":
                keyword = Core.INTEGER;
                break;
            case "return":
                keyword = Core.RETURN;
                break;
            case "do":
                keyword = Core.DO;
                break;
            case "new":
                keyword = Core.NEW;
                break;
            case "not":
                keyword = Core.NOT;
                break;
            case "and":
                keyword = Core.AND;
                break;
            case "or":
                keyword = Core.OR;
                break;
            case "out":
                keyword = Core.OUT;
                break;
            case "array":
                keyword = Core.ARRAY;
                break;
            case "then":
                keyword = Core.THEN;
                break;
            case "while":
                keyword = Core.WHILE;
                break;
            default:
                keyword = null;
                break;
        }

        return keyword;
    }

    /**
     * This method will every number started from {@code currentChar} until occurs any Symbol or string.
     * The range of Constant will be from 0 to 100003 (inclusive).
     * Any number that outside of this range will return token = Core.ERROR;
     * <p>
     * Set global variable token = Core.CONST; and constants = {number}
     *
     * @param currentChar the first char of Constant
     */
    private void determineConstants(char currentChar) throws IOException {
        token = Core.CONST;
        constants = new StringBuilder();
        constants.append(currentChar);

        boolean isContinue = true;
        do {
            nextUnicode = bufferedReader.read();
            int asciiCode = convertUnicodeToAscii(nextUnicode);

            // continue adding integers to constants.
            // if find any other letter or symbol, stop finding integer and return.
            if (asciiCode >= ASCII_0 && asciiCode <= ASCII_9) {
                char nextChar = (char) nextUnicode;
                constants.append(nextChar);

                // Integers from 0 to 100003 (inclusive), otherwise, set token = ERROR
                int tempInt = Integer.parseInt(constants.toString());
                if (tempInt > MAX_CONST) {
                    token = Core.ERROR;
                    // Print some meaningful error message.
                    System.out.println("ERROR: Integer " + constants.toString() + " is too large. The range of Integer should be between 0 and 100003");
                    return;
                }
            } else {
                isContinue = false;
            }
        } while (isContinue);
    }

    /**
     * This method determine whether {@code currentChar} is a valid symbol.
     * Assign symbol ":=" is the only one that has 2 chars.
     *
     * @param currentChar the first char of Symbol
     */
    private void determineSymbols(char currentChar) throws IOException {
        nextUnicode = bufferedReader.read();
        char nextChar = (char) nextUnicode;

        switch (currentChar) {
            case '+':
                token = Core.ADD;
                break;
            case '-':
                token = Core.SUBTRACT;
                break;
            case '*':
                token = Core.MULTIPLY;
                break;
            case '/':
                token = Core.DIVIDE;
                break;
            case '=':
                token = Core.EQUAL;
                break;
            case '<':
                token = Core.LESS;
                break;
            case ';':
                token = Core.SEMICOLON;
                break;
            case '.':
                token = Core.PERIOD;
                break;
            case ',':
                token = Core.COMMA;
                break;
            case '(':
                token = Core.LPAREN;
                break;
            case ')':
                token = Core.RPAREN;
                break;
            case '[':
                token = Core.LBRACE;
                break;
            case ']':
                token = Core.RBRACE;
                break;
            case ':':
                // This is only one needs to determine between ":=" and "="
                if (nextChar == '=') {
                    nextUnicode = bufferedReader.read();
                    token = Core.ASSIGN;
                } else {
                    token = Core.COLON;
                }
                break;
            default:
                token = Core.ERROR;

                System.out.println("ERROR: The symbol '" + currentChar + "' is not a valid symbol.");
                break;
        }
    }

    /**
     * The I/O has reached the end of file.
     * Set global token = Core.EOS;
     * Close all I/O stream.
     */
    private void endOfFile() {
        try {
            token = Core.EOS;

            // close readers
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: unable to close file stream!!!");
            System.exit(1);
        }
    }

    /**
     * Return the current token
     *
     * @return the value of {@code currentToken}
     */
    public Core currentToken() {
        return token;
    }

    /**
     * Return the identifier string
     *
     * @return the value of {@code id}
     */
    public String getId() {
        return id.toString();
    }

    /**
     * Return the constant value
     *
     * @return the value of {@code constants}
     */
    public int getConst() {
        return Integer.parseInt(constants.toString());
    }

}
