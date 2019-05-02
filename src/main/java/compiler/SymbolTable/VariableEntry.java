package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class VariableEntry extends AVEntry
{
    public VariableEntry(String name, TokenType type)
    {
        super(name);
        this.type = type;
    }

    public VariableEntry(String name, int address, TokenType type)
    {
        super(name, address);
        this.type = type;
    }

    @Override
    public boolean isVariable()
    {
        return true;
    }

    public String toString()
    {
        return type + " variable";
    }
}
