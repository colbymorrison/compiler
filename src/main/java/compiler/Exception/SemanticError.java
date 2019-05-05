package compiler.Exception;
import compiler.Lexer.Token;

/**
 * Semantic Action Error.
 */
public class SemanticError extends CompilerError
{
    // Constructors

    SemanticError(String message)
    {
        super(message);
    }

    SemanticError(String message, Token token)
    {
        super(message, token.GetRow(), token.GetCol());
    }

}
