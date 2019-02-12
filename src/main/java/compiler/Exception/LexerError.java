package compiler.Exception;

/**
 * Lexer errors, we define static methods to create various specific error cases.
 */
public class LexerError extends CompilerError {

    private LexerError(String message) {
        super(message);
    }

    /**
     * Error case for invalidCharacter
     *
     * @param ch  the character that was invalid
     * @param row row in file error occurred
     * @param col column in file error occurred
     * @return LexerError object to be thrown
     */
    public static LexerError invalidCharacter(char ch, int row, int col) {
        return new LexerError("Invalid character " + ch + rowCol(row, col));
    }

    /**
     * Error case for an invalid constant
     *
     * @param row row in file error occurred
     * @param col column in file error occurred
     * @return LexerError object to be thrown
     */
    public static LexerError invalidConstant(int row, int col) {
        return new LexerError("Invalid Constant" + rowCol(row, col));
    }

    /**
     * Error case for an invalid comment
     *
     * @param row row in file error occurred
     * @param col column in file error occurred
     * @return LexerError object to be thrown
     */
    public static LexerError invalidComment(int row, int col) {
        return new LexerError("Invalid Comment" + rowCol(row, col));
    }

    /**
     * Error case for an id that's too long
     *
     * @param row row in file error occurred
     * @param col column in file error occurred
     * @return LexerError object to be thrown
     */
    public static LexerError idTooLong(String id, int row, int col) {
        return new LexerError("Intentifier '" + id + "' is too long" + rowCol(row, col));
    }

    /**
     * Error case for an ioError, not much we can do here
     *
     * @return LexerError object to be thrown
     */
    public static LexerError ioError(String s) {
        return new LexerError("Lexer Error: IO Error, " + s);
    }
} 