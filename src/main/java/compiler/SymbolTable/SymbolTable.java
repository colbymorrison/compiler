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

    public void search(String key, SymbolTableEntry value) {
        table.putIfAbsent(key, value);
    }
}
