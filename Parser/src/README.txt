Zhao Liu

Core.java
        It's an enumeration class containing all necessary Constants from Keywords, Symbols, and four categories: CONST, ID, EOS, and ERROR.
Main.java
        It's the main entry for the project of Scanner.
Scanner.java
        The class implements the main function of the project. It's responsible to find the token and return the token the scanner is currently on.

Procedure.java
        This is the class that represents parser tree. Every method like "parse()", "semanticChecking()", and "print()" will invoke this class first.
        Represent non-terminal "<procedure>" in Context-Free Grammar

DeclarationSequence.java
        Represent non-terminal "<decl-seq>" in Context-Free Grammar

StatementSequence.java
        Represent non-terminal "<stmt-seq>" in Context-Free Grammar

Declaration.java
        Represent non-terminal "<decl>" in Context-Free Grammar

DeclarationInteger.java
        Represent non-terminal "<decl-integer>" in Context-Free Grammar

DeclarationArray.java
        Represent non-terminal "<decl-array>" in Context-Free Grammar

Statement.java
        Represent non-terminal "<stmt>" in Context-Free Grammar

Assign.java
        Represent non-terminal "<assign>" in Context-Free Grammar

Out.java
        Represent non-terminal "<out>" in Context-Free Grammar

In.java
        Represent non-terminal "<in>" in Context-Free Grammar

If.java
        Represent non-terminal "<if>" in Context-Free Grammar

Loop.java
        Represent non-terminal "<loop>" in Context-Free Grammar

Condition.java
        Represent non-terminal "<cond>" in Context-Free Grammar

Compare.java
        Represent non-terminal "<cmpr>" in Context-Free Grammar

Expression.java
        Represent non-terminal "<expr>" in Context-Free Grammar

Term.java
        Represent non-terminal "<term>" in Context-Free Grammar

Factor.java
        Represent non-terminal "<factor>" in Context-Free Grammar

Variable.java
        Store variable's name, type, and value for performing Semantic Checking


Special features: The program will take an input text file and output a stream of "tokens". The text file contains program codes.
                The tokens will be divided into four categories, CONST, ID, EOS, and ERROR.
                For example, the text file "procedure array ARRAY" will produce tokens "PROCEDURE ARRAY ID[ARRAY]".
                parse() method will generate a parse tree for the input program, using the top-down recursive descent approach described in class.
                semanticChecking() method will perform Semantic Check.
                print() method will produce "pretty" code with the appropriate indentation

Parse Tree: This program will perform Recursive Descent Parsing to analyze input source code.
            Procedure.java is the main entry class for parsing, it would be a root node for parsing tree.
            parse() method will generate a parse tree for the input program, using the top-down recursive descent approach described in class.
            semanticChecking() method will perform Semantic Check.
            print() method will produce "pretty" code with the appropriate indentation
            There are a bunch of classes represent Non-terminal during parsing and generating parsing tree objects.
            I have listed all the classes above and how they are represented in Context-Free Grammar and parse tree.
            Each class will have exactly same method name with Procedure.java to perform their own jobs.

Testing: I will test most simple input source code first. If there is not any error, I would test some complex one.
            When I occur some problems or errors, I would add more print statement to look at what the value of current object.
            I will also use Debug tool provided by IDE, but these kinds of tool may not work very well since we are using Recursive Descent Parsing algorithm.


Known bugs: None