package compiler.SymbolTable;

import compiler.Lexer.Token;
import compiler.Lexer.TokenType;

public class ConstantEntry extends SymbolTableEntry {
    private String name;
    private TokenType type;

    public ConstantEntry(String name, TokenType type) {
        this.name = name;
        this.type = type;
    }


}
