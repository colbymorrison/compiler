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
    private final Quadruples quads = new Quadruples();
    private boolean insert = true;
    private final boolean global = true;
    private boolean array = false;
    private int globalStore = 0;
    private int globalMemory = 0;
    private int localMemory = 0;
    private int tempCt = 0;

    /**
     * Constructor, insert default entries into global table
     */
    public SemanticAction() {
        try {
            SymbolTableEntry entry = new ProcedureEntry("READ", 0, new ArrayList<>(), true);
            entry.setReserved(true);
            globalTable.insert(entry);

            entry = new ProcedureEntry("WRITE", 0, new ArrayList<>(), true);
            entry.setReserved(true);
            globalTable.insert(entry);

            entry = new ProcedureEntry("MAIN", 0, new ArrayList<>(), true);
            entry.setReserved(true);
            globalTable.insert(entry);

            entry = new IODeviceEntry("INPUT", true);
            entry.setReserved(true);
            globalTable.insert(entry);

            entry = new IODeviceEntry("OUTPUT", true);
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

    // Semantic Actions
    /**
     * Semantic action 3, handles declarations of arrays and variables
     *
     * @throws SymbolTableError if an id is added to the local or global symbol table that already exists
     */
    private void three() throws SymbolTableError {
        TokenType type = stack.pop().getType();
        // If we have an array figure out memory based on bounds
        // Otherwise its a variable so memory is 1
        if (array) {
            int upperBound = Integer.parseInt(stack.pop().getValue().toString());
            int lowerBound = Integer.parseInt(stack.pop().getValue().toString());
            int memorySize = (upperBound - lowerBound) + 1;

            while (!stack.isEmpty() && stack.peek().getType() == TokenType.IDENTIFIER) {
                String name = stack.pop().getValue().toString();
                // Create array or variable entry
                ArrayEntry entry = new ArrayEntry(name, type, upperBound, lowerBound);

                // Add to local or global symbol table
                if (global) {
                    entry.setGlobal(true);
                    entry.setAddress(globalMemory);
                    globalTable.insert(entry);
                    globalMemory += memorySize;
                } else {
                    entry.setGlobal(false);
                    entry.setAddress(localMemory);
                    localTable.insert(entry);
                    localMemory += memorySize;
                }
            }
        } else {
            while (!stack.isEmpty() && stack.peek().getType() == TokenType.IDENTIFIER) {
                String name = stack.pop().getValue().toString();
                create(name, type);
            }
        }
        array = false;
    }


    /**
     * Semantic action 9, adds the name of the program to the global table
     *
     * @throws SymbolTableError
     */
    private void nine() throws SymbolTableError {
        stack.pop();
        stack.pop();
        Token id3 = stack.pop();

        SymbolTableEntry entry = new ProcedureEntry(id3.toString(), 0, new ArrayList<>(), true);
        entry.setReserved(true);
        globalTable.insert(entry);
        insert = false;

        generate("call", "main", "0");
        generate("exit");
    }


    /**
     * Gets the address of symbol table entry
     *
     * @param ste array, variable, or constant entry
     * @return address of entry
     */
    private int getSTEAddress(SymbolTableEntry ste) throws SymbolTableError {
        int address = 0;
        if (ste.isArray() || ste.isVariable()) {
            // array entries and variable entries are
            // assigned address when they are initialized
            ArrVarEntry entry = (ArrVarEntry) ste;
            address = entry.getAddress();
        } else if (ste.isConstant()) {
            // constants do not have an address, and a
            // temporary variable must be created to store it
            ConstantEntry entry = (ConstantEntry) ste;
            VariableEntry temp = create("temp", entry.getType());
            // move the constant into the temporary variable
            generate("move", ste.getName(), temp);
            // return the address of the temporary variable
            address = temp.getAddress();
        }
        return address;
    }

    private String getSTEPrefix(SymbolTableEntry ste) {
        if (ste.isParameter())
            return "^%";
        else if (ste.isGlobal())
            return "_";
        else
            return "%";
    }


    // Generate Methods
    private void generate(String tviCode, SymbolTableEntry[] operands) throws SymbolTableError {
        String[] quadEntry = new String[operands.length + 1];
        quadEntry[0] = tviCode;

        for (int i = 0; i < operands.length; i++) {
            SymbolTableEntry operand = operands[i];
            quadEntry[i] = getSTEPrefix(operand) + getSTEAddress(operand);
        }
        quads.addQuad(quadEntry);
//        quads.incrementNextQuad();
    }

    private void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, SymbolTableEntry operand3) throws SymbolTableError {
        generate(tviCode, new SymbolTableEntry[]{operand1, operand2, operand3});
    }

    private void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2) throws SymbolTableError {
        generate(tviCode, new SymbolTableEntry[]{operand1, operand2});
    }

    private void generate(String tviCode, SymbolTableEntry operand1) throws SymbolTableError {
        generate(tviCode, new SymbolTableEntry[]{operand1});
    }

    private void generate(String tviCode, String operand1, String operand2) throws SymbolTableError {
        quads.addQuad(new String[]{tviCode, operand1, operand2});
//        quads.incrementNextQuad();
    }

    private void generate(String tviCode) {
        quads.addQuad(new String[]{tviCode});
//        quads.incrementNextQuad();
    }

    private void generate(String tviCode, String operand1, SymbolTableEntry operand2) throws SymbolTableError {
        String[] quadEntry = new String[3];
        quadEntry[0] = tviCode;
        quadEntry[1] = operand1;
        quadEntry[2] = getSTEPrefix(operand2) + getSTEAddress(operand2);
    }


    private VariableEntry create(String name, TokenType type) throws SymbolTableError {
        if (name.equals("temp")) {
            name = "temp" + tempCt;
            tempCt++;
        }
        VariableEntry ve = new VariableEntry(name, type);
        // store the address as negative to distinguish between
        // temporary variables
        ve.setGlobal(global);
        if (global) {
            ve.setAddress(globalMemory);
            globalMemory++;
            globalTable.insert(ve);
        } else {
            ve.setAddress(localMemory);
            localMemory++;
            localTable.insert(ve);
        }
        return ve;
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
