package compiler;

import compiler.Exception.CompilerError;
import compiler.Lexer.Lexer;
import compiler.Parser.Parser;

/**
 * Main wrapper class for the whole compiler
 */
class Compiler
{
    private final Parser parser;

    /**
     * Create a parser with a lexer that uses the file path
     *
     * @param filePath file path to Compile
     * @param debug    should parser/semantic action debug info be printed?
     */
    Compiler(String filePath, boolean debug)
    {
        this.parser = new Parser(new Lexer(filePath), debug);
    }

    /**
     * Start the compiler by starting the parser
     */
    void Compile()
    {
        try
        {
            System.out.println(parser.Parse());
        } catch (CompilerError e)
        {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
