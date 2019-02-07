package compiler.Exception;

/**
 * Generic compiler error
 */
public class CompilerError extends Exception {
    public CompilerError(String message) {
        super(message);
    }

    /**
     * Prints out row and column nicely.
     *
     * @param row row of file error occurred
     * @param col column of file error occurred
     * @return String in nice format.
     */
    protected static String rowCol(int row, int col) {
        return " at line " + row + ", character " + col;
    }
}