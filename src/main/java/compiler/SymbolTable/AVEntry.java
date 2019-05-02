package compiler.SymbolTable;

public abstract class AVEntry extends SymbolTableEntry
{
    private int address;
    private boolean parameter;

    AVEntry(String name, int address)
    {
        super(name);
        this.address = address;
    }

    AVEntry(String name)
    {
        super(name);
    }

    public boolean isParameter()
    {
        return parameter;
    }

    public void setParameter()
    {
        this.parameter = true;
    }

    public void setAddress(int address)
    {
        this.address = address;
    }

    public int getAddress()
    {
        return address;
    }

}
