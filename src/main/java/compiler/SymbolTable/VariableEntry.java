package compiler.SymbolTable;

import compiler.Lexer.TokenType;

/**
 * Variable Entry class.
 */
public class VariableEntry extends AVEntry
{
    /**
     * Constructor
     *
     * @param name name of the variable.
     * @param type type of the variable
     */
    public VariableEntry(String name, TokenType type)
    {
        super(name);
        this.Type = type;
    }

    /**
     * Constructor
     *
     * @param name    name of the variable.
     * @param address address of the variable.
     * @param type    type of the variable.
     */
    public VariableEntry(String name, int address, TokenType type)
    {
        super(name, address);
        this.Type = type;
    }

    /**
     * This is a variable!
     */
    @Override
    public boolean isVariable()
    {
        return true;
    }

    /**
     * toString for debugging.
     */
    @Override
    public String toString()
    {
        return Type + " variable";
    }
}
