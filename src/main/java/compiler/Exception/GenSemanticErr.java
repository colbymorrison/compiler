package compiler.Exception;

import compiler.Lexer.Token;
import compiler.SemanticAction.EType;
import compiler.SymbolTable.FPEntry;
import compiler.SymbolTable.SymbolTableEntry;

/**
 * A factory for Semantic Errors, allows Semantic Errors to be generated from an object
 * instead of being static in the SemanticError class
 */
public class GenSemanticErr
{
    /**
     * Undeclared variable error.
     */
    public SemanticError undeclaredVariable(Token token)
    {
        return new SemanticError("Variable " + token.GetValue() + " is undeclared", token);
    }

    /**
     * Type mismatch error.
     */
    public SemanticError typeMismatch(String t1, String t2, Token token)
    {
        return new SemanticError("Types " + t1 + " and " + t2 + " are incompatible", token);
    }

    /**
     * Invalid parameter to a MOD or DIV operator (not an integer).
     */
    public SemanticError badParameterType(String opcode, Token token)
    {
        return new SemanticError("Operands of the " + opcode.toLowerCase() +
                " operator must both be integers", token);
    }

    /**
     * Parameter passed to a function or procedure of incorrect type.
     */
    public SemanticError badParameterType(FPEntry func, SymbolTableEntry id, SymbolTableEntry t2, Token token)
    {
        return new SemanticError("Function " + func.getName() + " requires parameter of type " + t2.getType() + ". Identifier " + id.getName() +
                " has type " + id.getType(), token);
    }

    /**
     * Attempt to pass a function or procedure as a parameter.
     */
    public SemanticError badParameterType(FPEntry func, SymbolTableEntry param, Token token)
    {
        return new SemanticError(param.getName() + " cannot be used as a parameter to " + func.getName(), token);
    }

    /**
     * Wrong number of parameters passed to a function.
     */
    public SemanticError badNumberParams(FPEntry func, int req, Integer prov, Token token)
    {
        return new SemanticError("Function " + func.getName() + "requires " + req + " parameters. It was provided" +
                prov + " parameters", token);
    }

    /**
     * Invalid use of an arithmetic or relational operator.
     */
    public SemanticError eTypeError(EType eType, Token token)
    {
        String message = eType == EType.ARITHMETIC
                ? "Invalid use of arithmetic operator"
                : "Invalid use of relation operator";

        return new SemanticError(message, token);
    }

    /**
     * Invalid procedure call.
     */
    public SemanticError illegalProcedure(SymbolTableEntry id)
    {
        return new SemanticError(id.getName() + " is not a procedure");
    }

    /**
     * Array operation on id that is not an array.
     */
    public SemanticError idIsNotArray(SymbolTableEntry id, Token token)
    {
        return new SemanticError(id.getName() + " is not an array", token);
    }
}
