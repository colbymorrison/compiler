package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class ArrayEntry extends ArrVarEntry {
    private int upBound;
    private int lowBound;

    @Override
    public boolean isArray() {
        return true;
    }

    public ArrayEntry(String name, TokenType type, int upBound, int lowBound) {
        super(name, type);
        this.upBound = upBound;
        this.lowBound = lowBound;
    }

    public String toString() {
        return type + " array";
    }
}
