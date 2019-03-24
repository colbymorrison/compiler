package compiler.Exception;

import compiler.Lexer.Token;
import compiler.Lexer.TokenType;

public class SemanticError extends CompilerError {
    public SemanticError(String message) {
        super(message);
    }

    public static SemanticError undeclaredVariable(Token token) {
        return new SemanticError("Variable " + token.getValue() + "is undeclared " +
                CompilerError.lineMsg(token.getRow(), token.getCol()));
    }

    public static SemanticError typeMismatch(String t1, String t2, int row, int col) {
        return new SemanticError("Types " + t1 + " and " + t2 + " are incompatable " +
                CompilerError.lineMsg(row, col));
    }

    public static SemanticError badParameter(Token token) {
        return new SemanticError("Invalid parameter " + token + CompilerError.lineMsg(token.getRow(), token.getCol()));
    }
}
