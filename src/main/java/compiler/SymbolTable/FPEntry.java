package compiler.SymbolTable;

import java.util.List;

public class FPEntry extends SymbolTableEntry {
    private int params;
    private List<AVEntry> paramInfo;

    FPEntry(String name, int params, List<AVEntry> paramInfo){
        super(name);
        this.params = params;
        this.paramInfo = paramInfo;
    }

    FPEntry(String name){
        super(name);
    }

    public int getParams(){
        return params;
    }

    public void setParams(int params){
        this.params = params;
    }

    public void addParameter(AVEntry param){
        paramInfo.add(param);
    }

    public List<AVEntry> getParamInfo() {
        return paramInfo;
    }
}
