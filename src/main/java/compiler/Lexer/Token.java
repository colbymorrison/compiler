package compiler.Lexer;

/**
 * Class to model tokens
 *
 * @param <T> type of value, default is null
 */
public class Token<T> {
    private final TokenType type;
    private T value = null;
    private int row;
    private int col;

    /**
     * Constructor that accepts just a type. Used to create tokens with no value
     *
     * @param tokenType type of this token.
     */
    public Token(TokenType tokenType) {
        this.type = tokenType;
    }

    /**
     * Overloaded constructor that accepts a type and value.
     *
     * @param tokenType type of this token
     * @param value     value associated with this token
     */
    public Token(TokenType tokenType, T value) {
        this.type = tokenType;
        this.value = value;
    }

    /**
     * Getter for type
     *
     * @return type
     */
    public TokenType getType() {
        return type;
    }

    public T getValue(){
        return value;
    }


    public String toString() {
        // Make sure we print in same format as testfiles for easy testing
        String val;
        if (value == null)
            val = "None";
        else if (type == TokenType.ADDOP || type == TokenType.ASSIGNOP || type == TokenType.RELOP || type == TokenType.MULOP)
            val = value.toString();
        else
            val = "'" + value + "'";

        return "['" + type + "', " + val + "]";
    }

//TODO add this functionality
    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
