package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class VariableEntry extends SymbolTableEntry{
    private int address;
    private TokenType type;


    public VariableEntry(String name, TokenType type, boolean isGlobal) {
        super(name, isGlobal);
        this.type = type;
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

    @Override
    public boolean isVariable() {
        return true;
    }

    public String toString() {
        return type + " variable";
    }
}
