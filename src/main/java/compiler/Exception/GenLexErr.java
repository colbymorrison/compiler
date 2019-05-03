package compiler.Exception;

/**
 * A factory for LexerErrors, allows LexerErrors to be generated from an object
 * instead of being  in the LexerError class
 */
public class GenLexErr
{
    /**
     * Error case for invalidCharacter
     *
     * @param ch  the character that was invalid
     * @param row row in file error occurred
     * @param col column in file error occurred
     * @return LexerError object to be thrown
     */
    public LexerError InvalidCharacter(char ch, int row, int col)
    {
        return new LexerError("Invalid character " + ch, row, col);
    }

    /**
     * Error case for an invalid constant
     *
     * @param row row in file error occurred
     * @param col column in file error occurred
     * @return LexerError object to be thrown
     */
    public LexerError InvalidConstant(int row, int col)
    {
        return new LexerError("Invalid Constant", row, col);
    }

    /**
     * Error case for an invalid comment
     *
     * @param row row in file error occurred
     * @param col column in file error occurred
     * @return LexerError object to be thrown
     */
    public LexerError InvalidComment(int row, int col)
    {
        return new LexerError("Invalid Comment", row, col);
    }


    /**
     * Error case for an id that's too long
     *
     * @param row row in file error occurred
     * @param col column in file error occurred
     * @return LexerError object to be thrown
     */
    public LexerError IdTooLong(String id, int row, int col)
    {
        return new LexerError("Identifier '" + id + "' is too long", row, col);
    }

    /**
     * Error case for an ioError, not much we can do here
     *
     * @return LexerError object to be thrown
     */
    public LexerError IoError(String s)
    {
        return new LexerError("Lexer Error: IO Error, " + s);
    }

}
