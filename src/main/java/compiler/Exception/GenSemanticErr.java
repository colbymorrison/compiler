package compiler.Exception;

import compiler.Lexer.Token;
import compiler.SemanticAction.EType;
import compiler.SymbolTable.FPEntry;
import compiler.SymbolTable.SymbolTableEntry;

/**
 * A factory for SemanticErrors, allows Semantic Errors to be generated from an object
 * instead of being  in the LexerError class
 */
public class GenSemanticErr
{
    public SemanticError undeclaredVariable(Token token)
    {
        return new SemanticError("Variable " + token.GetValue() + " is undeclared", token);
    }

    public SemanticError typeMismatch(String t1, String t2, Token token)
    {
        return new SemanticError("Types " + t1 + " and " + t2 + " are incompatible", token);
    }

    public SemanticError badParameter(String message, Token token)
    {
        return new SemanticError(message, token);
    }

    public SemanticError badParameterType(FPEntry func, SymbolTableEntry id, SymbolTableEntry t2, Token token)
    {
        return new SemanticError("Function " + func.getName() + " requires parameter of type " + t2.getType() + ". Identifier " + id.getName() +
                " has type " + id.getType(), token);
    }

    public SemanticError badParameterType(FPEntry id, Token token)
    {
        return new SemanticError(id + " cannot be used as a parameter", token);
    }

    public SemanticError badNumberParams(FPEntry func, int req, Integer prov, Token token)
    {
        return new SemanticError("Function " + func.getName() + "requires " + req + " parameters. It was provided" +
                prov + " parameters", token);
    }

    public SemanticError eTypeError(EType eType, Token token)
    {
        String message = eType == EType.ARITHMETIC
                ? "Invalid use of arithmetic operator"
                : "Invalid use of relation operator";

        return new SemanticError(message, token);
    }

    public SemanticError illegalProcedure(SymbolTableEntry id)
    {
        return new SemanticError(id.getName() + " is not a procedure");
    }

    public SemanticError idIsNotArray(SymbolTableEntry id, Token token)
    {
        return new SemanticError(id.getName() + " is not an array", token);
    }
}
