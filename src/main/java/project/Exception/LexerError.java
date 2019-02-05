package project.Exception;

public class LexerError extends CompilerError {
    private static final long serialVersionUID = 1L;

    private LexerError(String message) {
        super(message);
    }

    public static LexerError invalidCharacter(char ch, int row, int col) {
        return new LexerError("Lexer Error: Invalid character " + ch + rowCol(row, col));
    }

    public static LexerError illFormedConstant(int row, int col) {
        return new LexerError("Lexer Error: Invalid Constant " + rowCol(row, col));
    }

    public static LexerError illFormedComment(int row, int col) {
        return new LexerError("Lexer Error: Invalid Comment " + rowCol(row, col));
    }

    public static LexerError idTooLong(int row, int col) {
        return new LexerError("Lexer Error: intentifier is too long " + rowCol(row, col));
    }

    public static LexerError ioError(String s) {
        return new LexerError("Lexer Error: IO Error, "+s);
    }
} 