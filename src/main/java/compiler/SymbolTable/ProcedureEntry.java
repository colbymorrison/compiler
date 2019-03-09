package compiler.SymbolTable;

import java.util.List;

public class ProcedureEntry extends SymbolTableEntry {
    private int params;
    private List<String> paramInfo;

    public ProcedureEntry(String name, int params, List<String> paramInfo, boolean isGlobal) {
        super(name, isGlobal);
        this.name = name;
        this.params = params;
        this.paramInfo = paramInfo;
    }

    public String toString(){
        return "Procedure[# params: " + params + ", paramInfo: " + paramInfo + "]";
    }

}
