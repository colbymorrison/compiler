package compiler.SymbolTable;

import java.util.List;

public class ProcedureEntry extends SymbolTableEntry {
    private String name;
    private int params;
    private List<String> paramInfo;

    public ProcedureEntry(String name, int params, List<String> paramInfo) {
        this.name = name;
        this.params = params;
        this.paramInfo = paramInfo;
    }

}
