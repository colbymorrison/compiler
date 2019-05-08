package compiler.Exception;

import compiler.Lexer.Token;

/**
 * Semantic Action Error.
 */
public class SemanticError extends CompilerError
{

    /**
     * Constructor
     */
    SemanticError(String message)
    {
        super(message);
    }

    /**
     * Constructor
     */
    SemanticError(String message, Token token)
    {
        super(message, token.GetRow(), token.GetCol());
    }

}
