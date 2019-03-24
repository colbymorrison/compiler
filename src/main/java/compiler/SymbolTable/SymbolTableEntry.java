package compiler.SymbolTable;

import compiler.Lexer.TokenType;

/**
 * Abstract symbol table entry
 */
public abstract class SymbolTableEntry {
    String name;
    private boolean reserved;
    private boolean isGlobal;

    SymbolTableEntry() {
    }

    SymbolTableEntry(String name, boolean isGlobal) {
        this.name = name;
        this.isGlobal = isGlobal;
    }

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

    public boolean isConstant() {
        return false;
    }
    public void setReserved(boolean res) {
        this.reserved = res;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setGlobal(boolean global) {
        isGlobal = global;
    }

    public String getName() {
        return name;
    }

    public TokenType getType(){
        return null;
    }

    public int getAddress(){
        return 0;
    }

    public void setAddress(int address){
    }

}
