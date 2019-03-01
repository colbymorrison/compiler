package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class ConstantEntry extends SymbolTableEntry {
    private TokenType type;


    public ConstantEntry(String name, TokenType type) {
        this.name = name;
        this.type = type;
    }

    public String toString() {
        return type + " constant";
    }


}
