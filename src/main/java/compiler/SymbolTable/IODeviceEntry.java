package compiler.SymbolTable;

public class IODeviceEntry extends SymbolTableEntry{
    public IODeviceEntry(String name, boolean isGlobal) {
        super(name, isGlobal);
    }

    public String toString(){
        return "IODevice";
    }
}
