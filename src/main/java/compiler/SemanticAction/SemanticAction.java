package compiler.SemanticAction;

import compiler.Exception.SymbolTableError;
import compiler.Lexer.Token;
import compiler.Lexer.TokenType;
import compiler.SymbolTable.*;

import java.util.*;

public class SemanticAction {
    private final SymbolTable globalTable = new SymbolTable(20);
    private final SymbolTable constantTable = new SymbolTable(20);
    private final SymbolTable localTable = new SymbolTable(20);
    private final Stack<Token> stack = new Stack<>();
    private boolean insert = true;
    private final boolean global = true;
    private boolean array = false;
    private int globalMemory = 0;
    private int localMemory = 0;

    /**
     * Constructor, insert default entries into global table
     */
    public SemanticAction() {
        try {
            SymbolTableEntry entry = new ProcedureEntry("READ", 0, new ArrayList<>());
            entry.setReserved(true);
            globalTable.insert(entry);

            entry = new ProcedureEntry("WRITE", 0, new ArrayList<>());
            entry.setReserved(true);
            globalTable.insert(entry);

            entry = new IODeviceEntry("INPUT");
            entry.setReserved(true);
            globalTable.insert(entry);

            entry = new IODeviceEntry("OUTPUT");
            entry.setReserved(true);
            globalTable.insert(entry);
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


    /**
     * Semantic action 9, adds the name of the program to the global table
     *
     * @throws SymbolTableError
     */
    private void nine() throws SymbolTableError{
        stack.pop();
        stack.pop();
        Token id3 = stack.pop();

        SymbolTableEntry entry = new ProcedureEntry(id3.toString(), 0, new ArrayList<>());
        entry.setReserved(true);
        globalTable.insert(entry);
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
