package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class ArrayEntry extends SymbolTableEntry {
    private String name;
    private int address;
    private TokenType type;
    private int upBound;
    private int lowBound;

    @Override
    public boolean isArray() {
        return true;
    }

    public ArrayEntry(String name, int address, TokenType type, int upBound, int lowBound) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.upBound = upBound;
        this.lowBound = lowBound;
    }
}
