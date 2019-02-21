# Compiler
My compiler project for CS331 at Vassar.
This project implements a compiler for the Vascal language.

Written in Java 8 with Gradle 5 as a build manager.

## How to Run
The `Main` class has a driver function to parse a file given as a command line argument. 
Compiling and running using gradle is very easy. From the `compiler_project` directory simply run `./gradlew run --args PATH` where `PATH` is the path to the file to compile.
This runs the `gradlew` script, which looks in the `build.gradle` file where I defined `compiler.Main` to be the main class.

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


 
