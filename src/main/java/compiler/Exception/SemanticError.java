package compiler.Exception;

import compiler.Lexer.Token;
import compiler.SemanticAction.EType;
import compiler.SymbolTable.FPEntry;
import compiler.SymbolTable.SymbolTableEntry;

public class SemanticError extends CompilerError {
    private SemanticError(String message) {
        super(message);
    }

    private SemanticError(String message, Token token){
       super(message, token.getRow(), token.getCol());
    }

    public static SemanticError undeclaredVariable(Token token) {
        return new SemanticError("Variable " + token.getValue() + " is undeclared", token);
    }

    public static SemanticError typeMismatch(String t1, String t2, Token token) {
        return new SemanticError("Types " + t1 + " and " + t2 + " are incompatible", token);
    }

    public static SemanticError badParameter(String message, Token token) {
        return new SemanticError(message, token);
    }

    public static SemanticError badParameterType(FPEntry func, SymbolTableEntry id, SymbolTableEntry t2, Token token){
        return new SemanticError("Function " + func.getName() + " requires parameter of type " + t2.getType() + ". Identifier " + id.getName() +
                " has type " + id.getType(), token);
    }

    public static SemanticError badParameterType(FPEntry id, Token token){
        return new SemanticError(id + " cannot be used as a parameter", token);
    }

    public static SemanticError badNumberParams(FPEntry func, int req, Integer prov, Token token){
        return new SemanticError("Function " + func.getName() + "requires " + req + " parameters. It was provided" +
                prov + " parameters", token);
    }

    public static SemanticError eTypeError(EType eType, Token token){
        String message = eType == EType.ARITHMETIC
                ? "Invalid use of arithmetic operator"
                : "Invalid use of relation operator";

        return new SemanticError(message, token);
    }

    public static SemanticError illegalProcedure(SymbolTableEntry id){
        return new SemanticError(id.getName() + " is not a procedure" );
    }

    public static SemanticError idIsNotArray(SymbolTableEntry id, Token token){
        return new SemanticError(id.getName() + " is not an array", token);
    }
}
