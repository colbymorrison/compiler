package compiler.SymbolTable;

/**
 * IODevice Symbol Table Entry.
 */
public class IODeviceEntry extends SymbolTableEntry
{
    /**
     * Constructor
     * @param name name of the device.
     */
    public IODeviceEntry(String name)
    {
        super(name);
    }

    /**
     * toString
     */
    @Override
    public String toString()
    {
        return "IODevice";
    }
}
