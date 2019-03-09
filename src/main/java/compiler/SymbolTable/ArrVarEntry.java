package compiler.SymbolTable;

import compiler.Lexer.TokenType;

/**
 * Intermediate subclass for Array and Variable entries
 * They both have an address field and are often have similar behavior
 * So it is useful to have this class
 */
public abstract class ArrVarEntry extends SymbolTableEntry {
    private int address;
    TokenType type;

    ArrVarEntry(String name, TokenType type) {
        this.name = name;
        this.type = type;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }
}
