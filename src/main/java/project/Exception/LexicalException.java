package project.Exception;

public class LexicalException extends CompilerException {
    private static final long serialVersionUID = 1L;

    private LexicalException(String message) {
        super(message);
    }

    public static LexicalException invalidCharacter(char ch, int row, int col) {
        return new LexicalException("Lexer Error: Invalid character " + ch + rowCol(row, col));
    }

    public static LexicalException illFormedConstant(int row, int col) {
        return new LexicalException("Lexer Error: Invalid Constant " + rowCol(row, col));
    }

    public static LexicalException illFormedComment(int row, int col) {
        return new LexicalException("Lexer Error: Invalid Comment " + rowCol(row, col));
    }

    public static LexicalException idTooLong(int row, int col) {
        return new LexicalException("Lexer Error: intentifier is too long " + rowCol(row, col));
    }

    public static LexicalException ioError(String s) {
        return new LexicalException("Lexer Error: IO Error, "+s);
    }
} 