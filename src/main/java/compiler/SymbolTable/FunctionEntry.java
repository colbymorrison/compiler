package compiler.SymbolTable;

import java.util.List;

public class FunctionEntry extends SymbolTableEntry {
    private int params;
    private List<String> paramInfo;
    private VariableEntry result;

    public FunctionEntry(String name, int params, List<String> paramInfo, VariableEntry result) {
        super(name);
        this.params = params;
        this.paramInfo = paramInfo;
        this.result = result;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    public String toString() {
        return "Function[# params: " + params + ", paramInfo: " + paramInfo + "result: " + result + "]";
    }
}
