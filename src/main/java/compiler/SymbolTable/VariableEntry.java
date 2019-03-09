package compiler.SymbolTable;

import compiler.Lexer.TokenType;

public class VariableEntry extends ArrVarEntry {
    @Override
    public boolean isVariable() {
        return true;
    }

    public VariableEntry(String name, TokenType type) {
        super(name, type);
    }

    public String toString() {
        return type + " variable";
    }
}
