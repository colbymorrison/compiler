package compiler.Exception;

import compiler.Lexer.Token;
import compiler.SemanticAction.EType;

public class SemanticError extends CompilerError {
    public SemanticError(String message) {
        super(message);
    }

    public static SemanticError undeclaredVariable(Token token) {
        return new SemanticError("Variable " + token.getValue() + " is undeclared" +
                CompilerError.lineMsg(token.getRow(), token.getCol()));
    }

    public static SemanticError typeMismatch(String t1, String t2, int row, int col) {
        return new SemanticError("Types " + t1 + " and " + t2 + " are incompatable" +
                CompilerError.lineMsg(row, col));
    }

    public static SemanticError badParameter(String message, Token token) {
        return new SemanticError(message + CompilerError.lineMsg(token.getRow(), token.getCol()));
    }

    public static SemanticError eTypeError(EType eType){
        return new SemanticError("Bad eType " + eType);
    }
}
