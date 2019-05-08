package compiler.SymbolTable;

import java.util.List;

/**
 * Procedure Symbol Table Entry.
 */
public class ProcedureEntry extends FPEntry
{
    /**
     * Constructor
     *
     * @param name      name of the procedure.
     * @param params    number of parameters.
     * @param paramInfo list of parameters.
     */
    public ProcedureEntry(String name, int params, List<AVEntry> paramInfo)
    {
        super(name, params, paramInfo);
    }

    /**
     * Constructor
     *
     * @param name name of the procedure.
     */
    public ProcedureEntry(String name)
    {
        super(name);
    }

    /**
     * This is a procedure!
     */
    @Override
    public boolean IsProcedure()
    {
        return true;
    }

    /**
     * toString for debugging.
     */
    @Override
    public String toString()
    {
        return "Procedure[# params: " + GetParams() + ", paramInfo: " + GetParamInfo() + "]";
    }

}
