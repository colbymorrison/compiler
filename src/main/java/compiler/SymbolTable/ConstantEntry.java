package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class ConstantEntry extends SymbolTableEntry
{

    public ConstantEntry(String name, TokenType type)
    {
        super(name);
        this.type = type;
    }

    public boolean isConstant()
    {
        return true;
    }

    public String toString()
    {
        return type + " constant";
    }
}
