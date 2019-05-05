package compiler.SymbolTable;

import compiler.Lexer.TokenType;

/**
 * Abstract symbol table entry
 */
public abstract class SymbolTableEntry
{
    String Name;
    TokenType Type;
    private boolean reserved = false;

    SymbolTableEntry(String name)
    {
        this.Name = name;
    }

    /**
     * Is this a variable?
     */
    public boolean isVariable()
    {
        return false;
    }

    /**
     * Is this a procedure?
     */
    public boolean IsProcedure()
    {
        return false;
    }

    /**
     * Is this a function?
     */
    public boolean IsFunction()
    {
        return false;
    }

    /**
     * Is this a parameter?
     */
    public boolean IsParameter()
    {
        return false;
    }

    /**
     * Is this a array?
     */
    public boolean IsArray()
    {
        return false;
    }

    /**
     * Is this a constant?
     */
    public boolean isConstant()
    {
        return false;
    }

    /**
     * Is this a reserved word?
     */
    public void setReserved(boolean res)
    {
        this.reserved = res;
    }

    /**
     * Getter for the name.
     */
    public String getName()
    {
        return Name;
    }

    /**
     * Setter for type.
     */
    public void setType(TokenType type)
    {
        this.Type = type;
    }

    /**
     * Getter for type.
     */
    public TokenType getType()
    {
        return Type;
    }

    /**
     * Getter for address, will be overridden by STEs that have addresses
     */
    public int GetAddress()
    {
        return 0;
    }

    //public void SetAddress(int address)
   // {
   // }

}
