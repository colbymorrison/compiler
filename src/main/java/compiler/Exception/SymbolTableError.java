package compiler.Exception;


public class SymbolTableError extends CompilerError
{

    public SymbolTableError(String name)
    {
        super("Identifier " + name + " already declared in scope");
    }

}
