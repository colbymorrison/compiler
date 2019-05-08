package compiler.Exception;

/**
 * Lexer error class.
 */
public class LexerError extends CompilerError
{
    /**
     * Constructor
     */
    LexerError(String message)
    {
        super(message);
    }

    /**
     * Constructor
     */
    LexerError(String message, int row, int col)
    {
        super(message, row, col);
    }
}