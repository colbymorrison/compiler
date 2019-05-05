package compiler.SymbolTable;

import compiler.Lexer.TokenType;

/**
 * Constant Symbol Table Entry.
 */
public class ConstantEntry extends SymbolTableEntry
{

    /**
     * Constructor
     *
     * @param name name of constant.
     * @param type Type of constant.
     */
    public ConstantEntry(String name, TokenType type)
    {
        super(name);
        this.Type = type;
    }

    /**
     * This is a constant!
     */
    @Override
    public boolean isConstant()
    {
        return true;
    }

    /**
     * toString for debugging
     */
    @Override
    public String toString()
    {
        return Type + " constant";
    }
}
