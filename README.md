# Compiler
My compiler project for CS331 at Vassar.
This project implements a compiler for the Vascal language.

Written in Java 8 with Gradle 5 as a build manager.

## How to Run
The `Main` class has a driver function to parse a file.
The first command line argument should be the path of the file to parse. Setting the second command line argument to `--debug` or `-d` enables debug mode, which prints information about the parse.

 
Compiling and running using gradle is very easy. From the `compiler_project` directory simply run `./gradlew run --args 'ARGS'` where `ARGS` are the command line args seperated by spaces.
This runs the `gradlew` script, which looks in the `build.gradle` file where I defined `compiler.Compiler` to be the main class.

## Submission 1 (Lexer) Notes
The table mapping keywords to TokenTypes is in the Lexer class currently. It should be in the symbol table
but I haven't implemented that yet.

## Submission 2 (Parser) Notes
### General Notes
Test files are in the `src/test/resources` directory. They include the lexer and parser-specific test files along with more general programs to test.
### Changes to Lexer Package
After seeing the previous notes I moved the logic to check for and ingore comments from the `Scan` class to the `Lexer` class. I also moved the check for if a character is valid to the `Lexer` class. 

I changed the EOF character to `$`.

In the `Lexer` class I added a try-catch in the constructor and the `getNextToken()` method so the program exits gracefully if an error occurs while Lexing.

I also added a few lines to better track what row and column of the input file each Token is associated with. Now tokens are associated with their first character. Also tokens read from the pushBack stack now should have the right location, as opposed to off by 1 or 2 as was the case in the previous version.

In the `Scan` class I added a `setMinCol()` method to allow `Lexer` objects to decrement the column in the input file as they push items to the stack. 


## Submission 3 (Symbol Table & Semantic Action 1) Notes
### General Notes
Test files are in the `src/test/resources` directory. They include the lexer and parser-specific test files along with more general programs to test.

### Semantic Action 1 Notes
Though I included the code that tests if the `global` variable is set, none of the actions yet actually set this variable.
So, declarations that should be local are added to the global tale. This causes some erronous error messages to get printed.

### Changes to Main Class
Now debug information about the parse is not enaled by default. You have to pass `--debug` or `-d` as the second command line argument to enable the print statements.

### Changes to Parser Package
Added Semantic Actions to the `Parser` class. This involved reading the augmented grammar file, tracking the previous token found, and adding a new case to the `parse()` function to call the appropriate semantic action. I also updated the `dumpStack()` method to print out debug info that includes semantic action info. The parser also now matches the `ENDOFFILE` token and exits when the stack is empty instead of leaving `ENDOFFILE` on the stack. 


## Submission 4 (Semantic Action 2) Notes
### General Notes
Test files are in the `src/test/resources` directory. They include the lexer, parser, and semantic action-specific test files along with more general programs to test.

### Semantic Action 2 Notes
Everything seems to be working well. I chose to use 2 semantic stacks, one that holds Symbol Table Entries and another that holds Tokens to avoid a mess
of casting every time something has to be popped. I also added some helper methods to avoid duplicate code in the semantic actions. I also ditched the
`Quadruples` class, opting instead to use a ArrayList of String arrays held in the Semantic Actions class. The Quadruples class had only loose wrappers
around ArrayList functions so I chose to just call them directly and moved the `print()` method into the Semantic Action class.

### Changes to SymbolTableEntry
Added a `type` field that is used by subclasses associated with a type.  

### Changes to main package
Added a `Compiler` class to drive the whole program. As of now it prints the generated intermediate code (or error message) to the screen.
In the future I assume we'll want to write the intermediate code to a file. 




 
