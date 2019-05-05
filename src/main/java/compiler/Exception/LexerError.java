package compiler.Exception;

/**
 * Lexer error class.
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