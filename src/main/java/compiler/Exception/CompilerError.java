package compiler.Exception;

/**
 * Generic compiler error
 */
public abstract class CompilerError extends Exception
{
    CompilerError(String message)
    {
        super(message);
    }

    CompilerError(String message, int row, int col)
    {
        super(message + " at line " + row + " column " + col);
    }
}

