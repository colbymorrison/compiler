package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class ArrayEntry extends SymbolTableEntry {
    private int upBound;
    private int lowBound;
    private int address;

    @Override
    public boolean isArray() {
        return true;
    }

    public ArrayEntry(String name, TokenType type, int upBound, int lowBound) {
        super(name);
        this.type = type;
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

    public int getLowBound(){
        return lowBound;
    }

    public String toString() {
        return type + " array";
    }
}
