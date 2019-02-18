package compiler.Exception;

/**
 * Generic compiler error
 */
public abstract class CompilerError extends Exception {
    public CompilerError(String message) {
        super(message);
    }

    public CompilerError(String message, int row, int col) {
        super(message + " at line " + row + ", character " + col);
    }
}

