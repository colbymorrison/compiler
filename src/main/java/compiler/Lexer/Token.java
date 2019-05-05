package compiler.Lexer;

/**
 * Class to model tokens
 *
 * @param <T> Type of value, default is null
 */
public class Token<T>
{
    private final TokenType Type;
    private T Value = null;
    private int Row;
    private int Col;

    /**
     * Constructor that accepts just a Type. Used to create tokens with no value
     *
     * @param tokenType Type of this token.
     */
    Token(TokenType tokenType)
    {
        this.Type = tokenType;
    }

    /**
     * Overloaded constructor that accepts a Type and value.
     *
     * @param tokenType Type of this token
     * @param value     value associated with this token
     */
    Token(TokenType tokenType, T value)
    {
        this.Type = tokenType;
        this.Value = value;
    }

    // Gets type
    public TokenType GetType()
    {
        return Type;
    }

    // Gets value
    public T GetValue()
    {
        return Value;
    }

    // Gets column
    public int GetCol()
    {
        return Col;
    }

    // Sets column
    void SetCol(int col)
    {
        this.Col = col;
    }

    // Gets row
    public int GetRow()
    {
        return Row;
    }

    // Sets row
    void SetRow(int row)
    {
        this.Row = row;
    }

    // To String
    @Override
    public String toString()
    {
        // Make sure we print in same format as testfiles for easy testing
        String val;
        if (Value == null)
            val = "None";
        else if (Type == TokenType.ADDOP || Type == TokenType.ASSIGNOP || Type == TokenType.RELOP || Type == TokenType.MULOP)
            val = Value.toString();
        else
            val = "'" + Value + "'";

        return "['" + Type + "', " + val + "]";
    }


}
