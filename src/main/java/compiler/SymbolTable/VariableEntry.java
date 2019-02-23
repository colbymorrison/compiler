package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class VariableEntry extends SymbolTableEntry {
    private String name;
    private int address;
    private TokenType type;

    public VariableEntry(String name, int address, TokenType type) {
        this.name = name;
        this.address = address;
        this.type = type;
    }
}
