package compiler.SymbolTable;

public class IODeviceEntry extends SymbolTableEntry{
    public IODeviceEntry(String name) {
        this.name = name;
    }

    @Override
    public boolean isReserved() {
        return true;
    }

    public String toString(){
        return "IODevice";
    }
}
