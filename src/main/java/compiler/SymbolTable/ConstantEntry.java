package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class ConstantEntry extends SymbolTableEntry {
    private TokenType type;


    public ConstantEntry(String name, TokenType type) {
        super(name);
        this.type = type;
    }

    @Override
    public TokenType getType() {
        return type;
    }

    public boolean isConstant() {
        return true;
    }

    public String toString() {
        return type + " constant";
    }
}
