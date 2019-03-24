package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class ArrayEntry extends SymbolTableEntry {
    private int upBound;
    private int lowBound;
    private int address;
    private TokenType type;

    @Override
    public boolean isArray() {
        return true;
    }

    public ArrayEntry(String name, TokenType type, int upBound, int lowBound, boolean isGlobal) {
        super(name, isGlobal);
        this.upBound = upBound;
        this.lowBound = lowBound;
    }

    @Override
    public void setAddress(int address) {
        this.address = address;
    }

    @Override
    public int getAddress() {
        return address;
    }

    @Override
    public TokenType getType(){
        return type;
    }

    public String toString() {
        return type + " array";
    }
}
