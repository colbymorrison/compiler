package compiler.SymbolTable;

import compiler.Lexer.TokenType;

/**
 * Abstract symbol table entry
 */
public abstract class SymbolTableEntry
{
    String name;
    TokenType type;
    private boolean reserved = false;

    SymbolTableEntry(String name)
    {
        this.name = name;
    }

    public boolean isVariable()
    {
        return false;
    }

    public boolean isProcedure()
    {
        return false;
    }

    public boolean isFunction()
    {
        return false;
    }

    public boolean isParameter()
    {
        return false;
    }

    public boolean isArray()
    {
        return false;
    }

    public boolean isConstant()
    {
        return false;
    }

    public void setReserved(boolean res)
    {
        this.reserved = res;
    }

    public String getName()
    {
        return name;
    }

    public void setType(TokenType type)
    {
        this.type = type;
    }

    public TokenType getType()
    {
        return type;
    }

    // Will be overridden by STEs that have addresses
    public int getAddress()
    {
        return 0;
    }

    public void setAddress(int address)
    {
    }

}
