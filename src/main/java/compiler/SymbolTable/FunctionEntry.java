package compiler.SymbolTable;

import java.util.List;

public class FunctionEntry extends SymbolTableEntry {
    private String name;
    private int params;
    private List<String> paramInfo;
    private VariableEntry result;

    public FunctionEntry(String name, int params, List<String> paramInfo, VariableEntry result) {
        this.name = name;
        this.params = params;
        this.paramInfo = paramInfo;
        this.result = result;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    public String toString() {
        return "Function";
    }
}
