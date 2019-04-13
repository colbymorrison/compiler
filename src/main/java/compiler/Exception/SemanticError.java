package compiler.Exception;

import compiler.Lexer.Token;
import compiler.SemanticAction.EType;
import compiler.SymbolTable.FPEntry;
import compiler.SymbolTable.SymbolTableEntry;

//TODO make these pretty
// Better design?
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

    public static SemanticError badParemeterType(FPEntry func, SymbolTableEntry id, SymbolTableEntry t2, Token token){
        return new SemanticError("Function " + func.getName() + " requires parameter of type " + t2.getType() + ". Identifier " + id.getName() +
                " has type " + id.getType() + CompilerError.lineMsg(token.getRow(), token.getCol()));
    }

    public static SemanticError badNumberParams(FPEntry func, int req, Integer prov, Token token){
        return new SemanticError("Function " + func.getName() + "requires " + req + " parameters. It was provided" +
                prov + " parameters" + CompilerError.lineMsg(token.getRow(), token.getCol()));
    }

    public static SemanticError eTypeError(EType eType, Token token){
        String message;
        if(eType == EType.ARITHMETIC)
            message = "Invalid use of arithmetic operator";
        else
            message = "Invalid use of relation operator";

        message += CompilerError.lineMsg(token.getRow(), token.getCol());
        return new SemanticError(message);
    }

    public static SemanticError illegalProcedure(SymbolTableEntry id){
        return new SemanticError(id.getName() + " is not a procedure" );
    }

    public static SemanticError idIsNotArray(SymbolTableEntry id, Token token){
        return new SemanticError(id.getName() + " is not an array"+CompilerError.lineMsg(token.getRow(), token.getCol()));
    }
}
