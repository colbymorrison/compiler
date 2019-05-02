package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class ArrayEntry extends AVEntry
{
    private int upBound;
    private int lowBound;

    @Override
    public boolean isArray()
    {
        return true;
    }

    public ArrayEntry(String name, TokenType type, int upBound, int lowBound)
    {
        super(name);
        this.type = type;
        this.upBound = upBound;
        this.lowBound = lowBound;
    }

    public ArrayEntry(String name, int address, TokenType type, int upBound, int lowBound)
    {
        super(name, address);
        this.type = type;
        this.upBound = upBound;
        this.lowBound = lowBound;
    }

    public int getLowBound()
    {
        return lowBound;
    }

    public int getUpBound()
    {
        return upBound;
    }

    public String toString()
    {
        return type + " array";
    }
}
