package compiler.SymbolTable;

public class IODeviceEntry extends SymbolTableEntry{
    public IODeviceEntry(String name) {
        this.name = name;
    }

    public String toString(){
        return "IODevice";
    }
}
