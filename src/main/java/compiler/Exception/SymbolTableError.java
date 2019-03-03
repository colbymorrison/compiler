package compiler.Exception;

public class SymbolTableError extends CompilerError {

    private String name; // Name of identifier that caused error

    public SymbolTableError(String name) {
        super(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
