package compiler.Exception;

/**
 * Lexer errors, we define static methods to create various specific error cases.
 */
public class LexerError extends CompilerError
{
    //Constructors

    LexerError(String message)
    {
        super(message);
    }

    LexerError(String message, int row, int col)
    {
        super(message, row, col);
    }
}