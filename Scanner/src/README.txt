Zhao Liu

Core.java
        It's an enumeration class containing all necessary Constants from Keywords, Symbols, and four categories: CONST, ID, EOS, and ERROR.
Main.java
        It's the main entry for the project of Scanner.
Scanner.java
        The class implements the main function of the project. It's responsible to find the token and return the token the scanner is currently on.

Special features: The program will take an input text file and output a stream of "tokens". The text file contains program codes.
                The tokens will be divided into four categories, CONST, ID, EOS, and ERROR.
                For example, the text file "procedure array ARRAY" will produce tokens "PROCEDURE ARRAY ID[ARRAY]".

Known bugs: None