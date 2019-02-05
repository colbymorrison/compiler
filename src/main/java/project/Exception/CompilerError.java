package project.Exception;

public class CompilerError extends Exception{
    private static final long serialVersionUID = 1L;

    public CompilerError(String message){
        super(message);
    }

    protected static String rowCol(int row, int col){
        return "at line " + row + ", character " + col;
    }
}