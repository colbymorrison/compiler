package compiler.SymbolTable;

import compiler.Exception.SymbolTableError;

import java.util.Hashtable;

/**
 * Models a Symbol Table. Essentially a Symbol Table is just a Hashtable
 * mapping the Names of entries to their SymbolTableEntry Objects.
 */
public class SymbolTable
{
    private final Hashtable<String, SymbolTableEntry> table;

    /**
     * Constructor
     */
    public SymbolTable()
    {
        table = new Hashtable<>();
    }

    /**
     * Searches for an entry in the table.
     *
     * @param key the key to search for
     * @return the value associated with that key or null if no such value exists
     */
    public SymbolTableEntry Search(String key)
    {
        return table.getOrDefault(key, null);
    }

    /**
     * Insert an entry into the symbol table
     *
     * @param value the entry to insert
     * @throws SymbolTableError if an entry with that Name already exists
     */
    public void Insert(SymbolTableEntry value) throws SymbolTableError
    {
        if (table.containsKey(value.Name))
            throw new SymbolTableError(value.Name);
        else
            // The Name field of the entry is the id
            table.put(value.Name, value);
    }

    /**
     * toString, uses the HashTable's toString
     */
    @Override
    public String toString()
    {
        return table.toString();
    }
}
