package compiler.Exception;

/**
 * Generic compiler error.
 */
public abstract class CompilerError extends Exception
{
    /**
     * Constructor that takes a message.
     */
    CompilerError(String message)
    {
        super(message);
    }

    /**
     * Constructor that takes a message, row and column number.
     */
    CompilerError(String message, int row, int col)
    {
        super(message + " at line " + row + " column " + col);
    }
}

