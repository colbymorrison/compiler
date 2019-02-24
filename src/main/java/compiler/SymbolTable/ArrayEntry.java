package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class ArrayEntry extends SymbolTableEntry {
    private int address;
    private TokenType type;
    private int upBound;
    private int lowBound;

    @Override
    public boolean isArray() {
        return true;
    }

    public ArrayEntry(String name, TokenType type, int upBound, int lowBound) {
        this.name = name;
        this.type = type;
        this.upBound = upBound;
        this.lowBound = lowBound;
    }

    public void setAddress(int address){
        this.address = address;
    }
}
