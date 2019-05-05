package compiler.SymbolTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for a Function or Procedure Symbol Table Entry.
 * These share functionality and are often used together in the Semantic Actions.
 */
public abstract class FPEntry extends SymbolTableEntry
{
    private int Params;
    private List<AVEntry> ParamInfo = new ArrayList<>();

    /**
     * Constructor.
     * @param name name of the entry.
     * @param params number of parameters in the entry.
     * @param paramInfo List of parameters, which are either arrays or variables.
     */
    FPEntry(String name, int params, List<AVEntry> paramInfo)
    {
        super(name);
        this.Params = params;
        this.ParamInfo = paramInfo;
    }

    /**
     * Constructor
     * @param name name of the entry.
     */
    FPEntry(String name)
    {
        super(name);
    }

    /**
     * Getter for number of parameters.
     */
    public int GetParams()
    {
        return Params;
    }

    /**
     * Setter for number of parameters.
     */
    public void SetParams(int params)
    {
        this.Params = params;
    }

    /**
     * Add a parameter to the list of parameters.
     */
    public void AddParameter(AVEntry param)
    {
        ParamInfo.add(param);
    }

    /**
     * Getter for parameter list.
     */
    public List<AVEntry> GetParamInfo()
    {
        return ParamInfo;
    }
}
