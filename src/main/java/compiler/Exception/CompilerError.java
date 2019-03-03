package compiler.Exception;

/**
 * Generic compiler error
 */
public abstract class CompilerError extends Exception {
    CompilerError(String message) {
        super(message);
    }

    CompilerError(String message, int row, int col) {
        super(message + lineMsg(row, col));
    }

    public static String lineMsg(int row, int col) {
        return " at line " + row + ", character " + col + "\n";
    }
}

