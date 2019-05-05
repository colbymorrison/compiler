package compiler.SymbolTable;

/**
 * Abstract class for array and variable entries.
 * They share common functionality and are often used together in the Semantic Actions.
 */
public abstract class AVEntry extends SymbolTableEntry
{
    // Address of entry
    private int Address;
    // Is this entry a parameter?
    private boolean Parameter;

    /**
     * Constructor.
     * @param name name of the entry
     * @param address address of the entry
     */
    AVEntry(String name, int address)
    {
        super(name);
        this.Address = address;
    }

    /**
     * Constructor.
     * @param name name of the entry.
     */
    AVEntry(String name)
    {
        super(name);
    }

    /**
     * Getter for parameter.
     */
    public boolean IsParameter()
    {
        return Parameter;
    }

    /**
     * Set parameter to true.
     */
    public void SetParameter()
    {
        this.Parameter = true;
    }

    /**
     * Setter for address.
     */
    public void SetAddress(int address)
    {
        this.Address = address;
    }

    /**
     * Getter for address.
     */
    public int GetAddress()
    {
        return Address;
    }

}
