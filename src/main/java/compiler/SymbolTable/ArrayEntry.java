package compiler.SymbolTable;

import compiler.Lexer.TokenType;

/**
 * Array Symbol Table Entry.
 */
public class ArrayEntry extends AVEntry
{
    private int UpBound;
    private int LowBound;

    @Override
    public boolean IsArray()
    {
        return true;
    }

    /**
     * Constructor.
     *
     * @param name     name of the array.
     * @param type     Type of values in the array.
     * @param upBound  upper size bound.
     * @param lowBound lower size bound.
     */
    public ArrayEntry(String name, TokenType type, int upBound, int lowBound)
    {
        super(name);
        this.Type = type;
        this.UpBound = upBound;
        this.LowBound = lowBound;
    }

    /**
     * Constructor.
     *
     * @param name     name of the array.
     * @param address  address of the array.
     * @param type     Type of values in the array.
     * @param upBound  upper size bound.
     * @param lowBound lower size bound.
     */
    public ArrayEntry(String name, int address, TokenType type, int upBound, int lowBound)
    {
        super(name, address);
        this.Type = type;
        this.UpBound = upBound;
        this.LowBound = lowBound;
    }


    /**
     * Lower bound getter
     */
    public int GetLowBound()
    {
        return LowBound;
    }

    /**
     * Upper bound getter
     */
    public int GetUpBound()
    {
        return UpBound;
    }

    /**
     * String representation for debugging.
     */
    @Override
    public String toString()
    {
        return Type + " array";
    }
}
