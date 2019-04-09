package compiler.SymbolTable;

import compiler.Lexer.TokenType;

import java.util.List;

public class FunctionEntry extends FPEntry {
    private VariableEntry result;

    public FunctionEntry(String name, int params, List<AVEntry> paramInfo, VariableEntry result) {
        super(name, params, paramInfo);
        this.result = result;
    }

    public FunctionEntry(String name, VariableEntry result){
        super(name);
        this.result = result;
    }

    public void setResultType(TokenType type){
        this.result.setType(type);
    }

    public VariableEntry getResult(){
        return result;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    public String toString() {
        return "Function[# params: " + getParams() + ", paramInfo: " + getParamInfo() + "result: " + result + "]";
    }
}
