package compiler;

import compiler.Exception.CompilerError;
import compiler.Lexer.Lexer;
import compiler.Parser.Parser;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Compiler class, wraps lexer and parser classes
 */
class Compiler
{
    private final Parser Prser;

    /**
     * Create a parser with a lexer that uses the file path
     *
     * @param filePath file path to Compile
     * @param debug    should parser/semantic action debug info be printed?
     */
    Compiler(String filePath, boolean debug) throws FileNotFoundException
    {
        File file = new File(filePath);

        if (!file.exists())
            throw new FileNotFoundException();

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
