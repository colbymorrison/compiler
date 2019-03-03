package compiler.SemanticAction;

import compiler.Exception.SymbolTableError;
import compiler.Lexer.Token;
import compiler.Lexer.TokenType;
import compiler.SymbolTable.*;

import java.util.*;

public class SemanticAction {
    private SymbolTable globalTable = new SymbolTable(20);
    private SymbolTable constantTable = new SymbolTable(20);
    private SymbolTable localTable = new SymbolTable(20);
    private Stack<Token> stack = new Stack<>();
    private boolean insert = true;
    private boolean global = true;
    private boolean array = false;
    private int globalMemory = 0;
    private int localMemory = 0;

    /**
     * Constructor, insert default entries into global table
     */
    public SemanticAction() {
        try {
            globalTable.insert(new ProcedureEntry("READ", 0, new ArrayList<>()));
            globalTable.insert(new ProcedureEntry("WRITE", 0, new ArrayList<>()));
            globalTable.insert(new IODeviceEntry("INPUT"));
            globalTable.insert(new IODeviceEntry("OUTPUT"));
        } catch (SymbolTableError e) {
            e.printStackTrace();
        }
    }

    /**
     * Called by parser, executes a semantic action
     *
     * @param num       the number semantic action to execute
     * @param token     token at which semantic action was invoked
     * @param prevToken previous token
     * @throws SymbolTableError if an id is added to the local or global symbol table that already exists
     */
    public void execute(int num, Token token, Token prevToken) throws SymbolTableError {
        switch (num) {
            case 1:
                insert = true;
                break;
            case 2:
                insert = false;
                break;
            case 3:
                three();
                break;
            case 4:
                stack.push(prevToken);
                break;
            case 6:
                array = true;
                break;
            case 7:
                stack.push(prevToken);
                break;
            case 9:
                nine();
                break;
            case 13:
                stack.push(prevToken);
                break;
            default:
                break;
        }
    }


    /**
     * Semantic action 3, handles declarations of arrays and variables
     *
     * @throws SymbolTableError if an id is added to the local or global symbol table that already exists
     */
    private void three() throws SymbolTableError {
        TokenType type = stack.pop().getType();
        if (array) {
            // Get bounds
            int upperBound = Integer.parseInt(stack.pop().getValue().toString());
            int lowerBound = Integer.parseInt(stack.pop().getValue().toString());
            int memorySize = (upperBound - lowerBound) + 1;

            // Add entries to array
            while (!stack.isEmpty() && stack.peek().getType() == TokenType.IDENTIFIER) {
                ArrayEntry entry = new ArrayEntry(stack.pop().getValue().toString(), type, upperBound, lowerBound);
                if (global) {
                    entry.setAddress(globalMemory);
                    globalTable.insert(entry);
                    globalMemory += memorySize;
                } else {
                    entry.setAddress(localMemory);
                    localTable.insert(entry);
                    localMemory += memorySize;
                }
            }
        } else { // Simple variable
            while (!stack.isEmpty() && stack.peek().getType() == TokenType.IDENTIFIER) {
                VariableEntry entry = new VariableEntry(stack.pop().getValue().toString(), type);
                if (global) {
                    entry.setAddress(globalMemory);
                    globalTable.insert(entry);
                    globalMemory++;
                } else {
                    entry.setAddress(localMemory);
                    localTable.insert(entry);
                    localMemory++;
                }
            }
        }
        array = false;
    }


    private void nine() throws SymbolTableError{
        stack.pop();
        stack.pop();
        Token id3 = stack.pop();

        globalTable.insert(new ProcedureEntry(id3.toString(), 0, new ArrayList<>()));
        insert = false;
    }


    // Getters
    public SymbolTable getGlobalTable() {
        return globalTable;
    }

    public SymbolTable getConstantTable() {
        return constantTable;
    }

    public SymbolTable getLocalTable() {
        return localTable;
    }


    public Stack<Token> getStack() {
        return stack;
    }
}
