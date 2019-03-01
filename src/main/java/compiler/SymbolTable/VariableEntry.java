package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class VariableEntry extends SymbolTableEntry {
    private int address;
    private TokenType type;

    @Override
    public boolean isVariable() {
        return true;
    }

    public VariableEntry(String name, TokenType type) {
        this.name = name;
        this.type = type;
    }

    public void setAddress(int address){
        this.address = address;
    }

    public String toString() {
        return type + " variable";
    }
}
