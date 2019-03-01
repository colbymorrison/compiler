package compiler.SymbolTable;

import java.util.Hashtable;

public class SymbolTable {
    private Hashtable<String, SymbolTableEntry> table;

    public SymbolTable(int initialCapacity) {
        table = new Hashtable<>(initialCapacity);
    }

    public void insert(String key, SymbolTableEntry value) {
        table.put(key, value);
    }

    public SymbolTableEntry search(SymbolTableEntry key) {
        return table.getOrDefault(key, null);
    }

    public void insert(SymbolTableEntry value){
        table.putIfAbsent(value.name, value);
    }

    public int size() {
        return table.size();
    }

    public String toString() {
        return table.toString();
    }
}
