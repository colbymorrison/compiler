public class Token<T> {
    private TokenType type;
    private T value;

    enum TokenType {
        PROGRAM, RESULT, IF, INTCONSTANT, COMMA, LEFTBRACKET,
        BEGIN, THEN, REALCONSTANT, SEMICOLON, RIGHTBRACKET,
        END, REAL, ELSE, RELOP, COLON, UNARYMINUS,
        VAR, ARRAY, WHILE, MULOP, LEFTPAREN, UNARYPLUS,
        FUNCTION, OF, DO, ADDOP, RIGHTPAREN, ENDMARKER,
        PROCEDURE, NOT, IDENTIFIER, ASSIGNOP, DOUBLEDOT, ENDOFFILE,
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
