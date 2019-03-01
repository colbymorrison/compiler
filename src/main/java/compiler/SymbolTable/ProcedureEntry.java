package compiler.SymbolTable;

import java.util.List;

public class ProcedureEntry extends SymbolTableEntry {
    private int params;
    private List<String> paramInfo;

    @Override
    public boolean isReserved() {
        return true;
    }

    public ProcedureEntry(String name, int params, List<String> paramInfo) {
        this.name = name;
        this.params = params;
        this.paramInfo = paramInfo;
    }

    public String toString(){
        return "Procedure";
    }

}
