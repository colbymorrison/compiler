package compiler.Exception;

/**
 * Symbol Table Error
 */
public class SymbolTableError extends CompilerError
{
    /**
     * Constructor.
     * The only type of error we can have is declaring an identifier more than once
     * in the same scope.
     */
    public SymbolTableError(String name)
    {
        super("Identifier " + name + " already declared in scope");
    }

}
