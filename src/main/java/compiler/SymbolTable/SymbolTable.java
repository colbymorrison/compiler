package compiler.SymbolTable;

import compiler.Exception.SymbolTableError;

import java.util.Hashtable;

public class SymbolTable {
    private final Hashtable<String, SymbolTableEntry> table;

    /**
     * Constructor
     *
     * @param initialCapacity the initial capacity for the SymbolTable
     */
    public SymbolTable(int initialCapacity) {
        table = new Hashtable<>(initialCapacity);
    }

    /**
     * Searches for an entry in the table.
     * @param key the key to search for
     * @return the value associated with that key or null if no such value exists
     */
    public SymbolTableEntry search(SymbolTableEntry key) {
        return table.getOrDefault(key, null);
    }

    /**
     * Insert an entry into the symbol table
     *
     * @param value the entry to insert
     * @throws SymbolTableError if an entry with that name already exists
     */
    public void insert(SymbolTableEntry value) throws SymbolTableError {
        // The name field of the entry is the id
        if (table.containsKey(value.name))
            throw new SymbolTableError(value.name);
        else
            table.put(value.name, value);
    }

    /**
     * @return size of the table
     */
    public int size() {
        return table.size();
    }

    public String toString() {
        return table.toString();
    }
}
