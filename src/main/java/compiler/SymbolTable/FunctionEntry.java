package compiler.SymbolTable;

import compiler.Lexer.TokenType;

/**
 * Function Symbol Table Entry.
 */
public class FunctionEntry extends FPEntry
{
    // Return value
    private VariableEntry Result;

    /**
     * Constructor
     * @param name name of the function.
     * @param result return value of the function.
     */
    public FunctionEntry(String name, VariableEntry result)
    {
        super(name);
        this.Result = result;
    }

    /**
     * Set return type.
     */
    public void SetResultType(TokenType type)
    {
        this.Result.setType(type);
    }

    /**
     * Get return value.
     */
    public VariableEntry GetResult()
    {
        return Result;
    }

    /**
     * This is a function!
     */
    @Override
    public boolean IsFunction()
    {
        return true;
    }

    /**
     * toString for debugging.
     */
    @Override
    public String toString()
    {
        return "Function[# params: " + GetParams() + ", paramInfo: " + GetParamInfo() + "result: " + Result + "]";
    }
}
