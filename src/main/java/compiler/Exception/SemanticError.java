package compiler.Exception;

import compiler.Lexer.Token;

public class SemanticError extends CompilerError
{
    SemanticError(String message)
    {
        super(message);
    }

    SemanticError(String message, Token token)
    {
        super(message, token.GetRow(), token.GetCol());
    }

}
