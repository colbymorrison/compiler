package compiler.SymbolTable;

import java.util.List;

public class ProcedureEntry extends FPEntry
{
    public ProcedureEntry(String name, int params, List<AVEntry> paramInfo)
    {
        super(name, params, paramInfo);
    }

    public ProcedureEntry(String name)
    {
        super(name);
    }

    public boolean isProcedure()
    {
        return true;
    }

    public String toString()
    {
        return "Procedure[# params: " + getParams() + ", paramInfo: " + getParamInfo() + "]";
    }

}
