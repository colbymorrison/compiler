package compiler.SymbolTable;

/**
 * Abstract symbol table entry
 */
public abstract class SymbolTableEntry {
    String name;
    private boolean reserved;

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
        return reserved;
    }

    public void setReserved(boolean res) {
        this.reserved = res;
    }
}
