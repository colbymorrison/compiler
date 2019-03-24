package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class ConstantEntry extends SymbolTableEntry {
    private TokenType type;


    public ConstantEntry(String name, TokenType type, boolean isGlobal) {
        super(name, isGlobal);
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
