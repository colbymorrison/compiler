package compiler.Exception;

import compiler.Lexer.Token;
import compiler.SemanticAction.EType;
import compiler.SymbolTable.FPEntry;
import compiler.SymbolTable.SymbolTableEntry;

public class SemanticError extends CompilerError
{
    SemanticError(String message)
    {
        super(message);
    }

    SemanticError(String message, Token token)
    {
        super(message, token.getRow(), token.getCol());
    }

}
