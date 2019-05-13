package compiler;

import compiler.Exception.CompilerError;
import compiler.Lexer.Lexer;
import compiler.Parser.Parser;

import java.io.File;

/**
 * Compiler class, wraps lexer and parser classes
 */
class Compiler
{
    private final Parser Prser;

    /**
     * Create a parser with a lexer that uses the file path
     *
     * @param file file path to Compile
     * @param debug    should parser/semantic action debug info be printed?
     */
    Compiler(File file, boolean debug)
    {
        this.Prser = new Parser(new Lexer(file), debug);
    }

    /**
     * Start the compiler by starting the parser
     */
    void Compile()
    {
        try
        {
            System.out.println(Prser.Parse());
        } catch (CompilerError e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
