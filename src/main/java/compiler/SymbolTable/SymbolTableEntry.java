package compiler.SymbolTable;

public abstract class SymbolTableEntry {
    public boolean isVariable() {
        return false;
    }

    public boolean isProcedure() {
        return false;
    }

    public boolean isFunction() {
        return false;
    }

    public boolean isFunctionResult() {
        return false;
    }

    public boolean isParameter() {
        return false;
    }

    public boolean isArray() {
        return false;
    }

    public boolean isReserved() {
        return false;
    }
}
