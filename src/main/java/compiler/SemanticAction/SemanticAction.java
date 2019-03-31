package compiler.SemanticAction;

import compiler.Exception.SemanticError;
import compiler.Exception.SymbolTableError;
import compiler.Lexer.Token;
import compiler.Lexer.TokenType;
import compiler.SymbolTable.*;

import java.util.*;

public class SemanticAction {
    private final SymbolTable globalTable = new SymbolTable(20);
    private final SymbolTable constantTable = new SymbolTable(20);
    private final SymbolTable localTable = new SymbolTable(20);
    private final Stack<Token> tokenStack = new Stack<>();
    private final Stack<SymbolTableEntry> steStack = new Stack<>();
    // List of quadruples, which we represent as string arrays
    private ArrayList<String[]> quads = new ArrayList<>();
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
        // Add dummy quad
        quads.add(new String[]{null, null, null, null});
        // Insert reserved words into the global table
        try {
            SymbolTableEntry entry = new ProcedureEntry("READ", 0, new ArrayList<>());
            entry.setReserved(true);
            globalTable.insert(entry);

            entry = new ProcedureEntry("WRITE", 0, new ArrayList<>());
            entry.setReserved(true);
            globalTable.insert(entry);

            entry = new ProcedureEntry("MAIN", 0, new ArrayList<>());
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
    public void execute(int num, Token token, Token prevToken) throws SemanticError, SymbolTableError {
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
                tokenStack.push(prevToken);
                break;
            case 6:
                array = true;
                break;
            case 7:
                tokenStack.push(prevToken);
                break;
            case 9:
                nine();
                break;
            case 13:
                tokenStack.push(prevToken);
                break;
            case 30:
                thirty(prevToken);
                break;
            case 31:
                thirtyOne(token);
                break;
            case 40:
                tokenStack.push(prevToken);
                break;
            case 41:
                fourtyOne();
                break;
            case 42:
                tokenStack.push(prevToken);
                break;
            case 43:
                fourtyThree();
                break;
            case 44:
                tokenStack.push(prevToken);
                break;
            case 45:
                fourtyFive();
                break;
            case 46:
                fourtySix(prevToken);
                break;
            case 48:
                fourtyEight();
                break;
            case 55:
                fiftyFive();
                break;
            case 56:
                fiftySix();
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
        TokenType type = tokenStack.pop().getType();
        SymbolTableEntry entry;
        int memorySize = 1;
        int upperBound = 0;
        int lowerBound = 0;
        // If we have an array figure out memory based on bounds
        // Otherwise its a variable so memory is 1
        if (array) {
            upperBound = Integer.parseInt(tokenStack.pop().getValue().toString());
            lowerBound = Integer.parseInt(tokenStack.pop().getValue().toString());
            memorySize = (upperBound - lowerBound) + 1;
        }

        while (!tokenStack.isEmpty() && tokenStack.peek().getType() == TokenType.IDENTIFIER) {
            String name = tokenStack.pop().getValue().toString();
            // Create array or variable entry
            entry = array
                    ? new ArrayEntry(name, type, upperBound, lowerBound)
                    : new VariableEntry(name, type);

            // Add to local or global symbol table
            if (global) {
                entry.setAddress(-1 * globalMemory);
                globalTable.insert(entry);
                globalMemory += memorySize;
            } else {
                entry.setAddress(localMemory);
                localTable.insert(entry);
                localMemory += memorySize;
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
        tokenStack.pop();
        tokenStack.pop();
        Token id3 = tokenStack.pop();

        SymbolTableEntry entry = new ProcedureEntry(id3.toString(), 0, new ArrayList<>());
        entry.setReserved(true);
        globalTable.insert(entry);
        insert = false;

        generate("call", "main", "0");
        generate("exit");
    }

    /**
     * Check to see if a variable has been declared
     * @param token identifier variable
     * @throws SemanticError if the variable has not been declared
     */
    private void thirty(Token token) throws SemanticError {
        SymbolTableEntry id = lookupId(token);
        if (id == null) {
            throw SemanticError.undeclaredVariable(token);
        }
        steStack.push(id);
    }

    /**
     * Variable assignment
     *
     * @param token to get row and column if an error occurs
     * @throws SymbolTableError
     * @throws SemanticError    if there is a type mismatch
     */
    private void thirtyOne(Token token) throws SymbolTableError, SemanticError {
        SymbolTableEntry id2 = steStack.pop();
        // offset will be implemented in later actions
        SymbolTableEntry offset = null;
        SymbolTableEntry id1 = steStack.pop();

        if (typeCheck(id1, id2) == 3)
            throw SemanticError.typeMismatch("Integer", "Real", token.getRow(), token.getCol());

        if (typeCheck(id1, id2) == 2) {
            VariableEntry temp = createTemp(TokenType.REAL);
            generate("ltof", id2, temp);
            if (offset == null)
                generate("move", temp, id1);
            else
                generate("stor", temp, offset, id1);
        } else {
            if (offset == null)
                generate("move", id2, id1);
            else
                generate("stor", id2, offset, id1);
        }
    }

    /**
     * Handles unary minus and plus
     *
     * @throws SymbolTableError
     */
    private void fourtyOne() throws SymbolTableError {
        SymbolTableEntry id = steStack.pop();
        Token sign = tokenStack.pop();
        if (sign.getType() == TokenType.UNARYMINUS) {
            VariableEntry temp = createTemp(id.getType());
            if (id.getType() == TokenType.INTEGER) {
                generate("uminus", id, temp);
            } else {
                generate("fuminus", id, temp);
            }
            steStack.push(temp);
        } else
            steStack.push(id);
    }

    /**
     * Handles arithmetic and comparison operations
     */
    private void fourtyThree() throws SymbolTableError {
        SymbolTableEntry id2 = steStack.pop();
        // this is one place where the operator from action 42 is popped
        Token operator = tokenStack.pop();
        // get the TVI opcode associated with the operator token
        // ex. for a token representing addition, opcode would be "add"
        String opcode = getOpCode(operator);
        SymbolTableEntry id1 = steStack.pop();

        if (typeCheck(id1, id2) == 0) {
            VariableEntry temp = createTemp(TokenType.INTEGER);
            generate(opcode, id1, id2, temp);
            steStack.push(temp);
        } else if (typeCheck(id1, id2) == 1) {
            VariableEntry temp = createTemp(TokenType.REAL);
            generate("f" + opcode, id1, id2, temp);
            steStack.push(temp);
        } else { // IS THIS RIGHT?
            VariableEntry temp1 = createTemp(TokenType.REAL);
            VariableEntry temp2 = createTemp(TokenType.REAL);
            generate("ltof", id1, temp1);
            generate("f" + opcode, temp1, id2, temp2);
            steStack.push(temp2);
        }
    }

    /**
     * Evaluate expression
     *
     * @throws SymbolTableError
     * @throws SemanticError
     */
    private void fourtyFive() throws SymbolTableError, SemanticError {
        // Pushed in #46
        SymbolTableEntry id2 = steStack.pop();
        // Pushed in #44
        Token operator = tokenStack.pop();
        // Pushed in #46
        String opcode = getOpCode(operator);
        SymbolTableEntry id1 = steStack.pop();

        if (typeCheck(id1, id2) != 0 && (opcode.equals("DIV") || opcode.equals("MOD"))) {
            // MOD and DIV require integer operands
            throw SemanticError.badParameter("Operands of the " + opcode.toLowerCase() +
                    " operator must both be integers", operator);
        }

        if (typeCheck(id1, id2) == 0) {
            if (opcode.equals("MOD")) {
                VariableEntry temp1 = createTemp(TokenType.INTEGER);
                VariableEntry temp2 = createTemp(TokenType.INTEGER);
                VariableEntry temp3 = createTemp(TokenType.INTEGER);
                generate("div", id1, id2, temp1);
                generate("mul", id2, temp1, temp2);
                generate("sub", id1, temp2, temp3);
                steStack.push(temp3);
            } else if (opcode.equals("div")) { // div or DIV??
                VariableEntry temp1 = createTemp(TokenType.REAL);
                VariableEntry temp2 = createTemp(TokenType.REAL);
                VariableEntry temp3 = createTemp(TokenType.REAL);
                generate("ltof", id1, temp1);
                generate("ltof", id2, temp2);
                generate("fdiv", temp1, temp2, temp3);
                steStack.push(temp3);
            } else {
                VariableEntry temp = createTemp(TokenType.INTEGER);
                generate(opcode, id1, id2, temp);
                steStack.push(temp);
            }
        } else if (typeCheck(id1, id2) == 1) {
            VariableEntry temp = createTemp(TokenType.REAL);
            generate("f" + opcode, id1, id2, temp);
            steStack.push(temp);
        } else {
            VariableEntry temp1 = createTemp(TokenType.REAL);
            VariableEntry temp2 = createTemp(TokenType.REAL);
            generate("ltof", id2, temp1);
            generate("f" + opcode, id1, temp1, temp2);
            steStack.push(temp2);
        }
    }


    /**
     * Push identifiers & constants onto the stack for evaluation in an expression
     *
     * @param token
     * @throws SemanticError
     * @throws SymbolTableError
     */
    private void fourtySix(Token token) throws SemanticError, SymbolTableError {
        if (token.getType() == TokenType.IDENTIFIER) {
            // look for the token in the global or local symbol table
            SymbolTableEntry id = lookupId(token);
            // if token is not found
            if (id == null)
                throw SemanticError.undeclaredVariable(token);

            steStack.push(id);
        } else if (token.getType() == TokenType.INTCONSTANT || token.getType() == TokenType.REALCONSTANT) {
            // look for the token in the constant symbol table
            SymbolTableEntry id = constantTable.search(token.getValue().toString());
            // if token is not found
            if (id == null) {
                id = new ConstantEntry(token.getValue().toString(), token.getType());
                constantTable.insert(id);
            }
            steStack.push(id);
        }

    }

    private void fourtyEight() throws SymbolTableError {
        // offset will be implemented in later actions
        SymbolTableEntry offset = null;
        if (offset != null) {
            SymbolTableEntry id = steStack.pop(); // IS THIS RIGHT??? I think 48 only gets called when top stack is type id
            VariableEntry temp = createTemp(id.getType());
            generate("load", id, offset, temp);
            steStack.push(temp);
        }
    }

    private void fiftyFive() {
        backpatch(globalStore, globalMemory);
        generate("free", Integer.toString(globalMemory));
        generate("PROCEND");
    }

    private void fiftySix() {
        generate("PROCBEGIN", "main");
        globalStore = quads.size();
        // the underscore as the second arguement in generate
        // is a placeholder that will be filled in later by backpatch
        generate("alloc", "_");
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
            address = Math.abs(ste.getAddress());
        } else if (ste.isConstant()) {
            // constants do not have an address, and a
            // temporary variable must be created to store it
            ConstantEntry entry = (ConstantEntry) ste;
            VariableEntry temp = createTemp(entry.getType());
            // move the constant into the temporary variable
            generate("move", ste.getName(), temp);
            // return the address of the temporary variable
            address = Math.abs(temp.getAddress());
        }
        return address;
    }

    private String getSTEPrefix(SymbolTableEntry ste) {
        if (global) {
            return "_";
        } else { // local
            SymbolTableEntry entry = localTable.search(ste.getName());
            if (entry == null)  // entry is a global variable
                return "_";
            else
                return "%";
        }
    }


    // Generate Methods

    private void generate(String tviCode, SymbolTableEntry[] operands) throws SymbolTableError {
        String[] quadEntry = new String[operands.length + 1];
        quadEntry[0] = tviCode;

        for (int i = 0; i < operands.length; i++) {
            SymbolTableEntry operand = operands[i];
            quadEntry[i + 1] = getSTEPrefix(operand) + getSTEAddress(operand);
        }
        quads.add(quadEntry);
    }

    private void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, SymbolTableEntry operand3) throws SymbolTableError {
        generate(tviCode, new SymbolTableEntry[]{operand1, operand2, operand3});
    }

    private void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2) throws SymbolTableError {
        generate(tviCode, new SymbolTableEntry[]{operand1, operand2});
    }

    private void generate(String tviCode, String operand1) {
        quads.add(new String[]{tviCode, operand1});
    }

    private void generate(String tviCode, String operand1, String operand2) {
        quads.add(new String[]{tviCode, operand1, operand2});
    }

    private void generate(String tviCode) {
        quads.add(new String[]{tviCode});
    }

    private void generate(String tviCode, String operand1, SymbolTableEntry operand2) throws SymbolTableError {
        String[] quadEntry = new String[3];
        quadEntry[0] = tviCode;
        quadEntry[1] = operand1;
        quadEntry[2] = getSTEPrefix(operand2) + getSTEAddress(operand2);
        quads.add(quadEntry);
    }

    // Helper methods

    /**
     * Creates a new variable entry and inserts it into the proper symbol table.
     *
     * @param type type of variable
     * @return the vriable entry
     * @throws SymbolTableError if a variable with this name is already in the symbol table
     */
    private VariableEntry createTemp(TokenType type) throws SymbolTableError {
        tempCt++;

        VariableEntry ve = new VariableEntry("$$temp" + tempCt, type);
        // Global or local?
        if (global) {
            ve.setAddress(-1 * globalMemory);
            globalMemory++;
            globalTable.insert(ve);
        } else {
            ve.setAddress(localMemory);
            localMemory++;
            localTable.insert(ve);
        }
        return ve;
    }

    /**
     * Checks type of 2 integers/reals
     *
     * @param id1 integer or real
     * @param id2 integer or real
     * @return 0-3 based on relationship
     */
    private int typeCheck(SymbolTableEntry id1, SymbolTableEntry id2) {
        TokenType type1 = id1.getType();
        TokenType type2 = id2.getType();
        boolean int1 = type1 == TokenType.INTEGER || type1 == TokenType.INTCONSTANT;
        boolean int2 = type2 == TokenType.INTEGER || type2 == TokenType.INTCONSTANT;

        if (int1 && int2)
            return 0;
        else if (!int1 && !int2)
            return 1;
        else if (int2)
            return 2;
        else
            return 3;
    }

    /**
     * Looks up an id in the symbol table
     *
     * @param token with type Identifier ??
     * @return the symbol table entry if it exists or null otherwise
     */
    private SymbolTableEntry lookupId(Token token) {
        String id = token.getValue().toString();
        // first look in the local table
        SymbolTableEntry ste = localTable.search(id);
        // if id is not in the local table
        if (ste == null)
            // then look in the global table
            ste = globalTable.search(id);

        return ste;
    }

    /**
     * Replaces second index of quadruple with a new address.
     *
     * @param i index of quadruple to replace.
     * @param x address to insert.
     */
    private void backpatch(int i, int x) {
        quads.get(i)[1] = Integer.toString(x);
    }

    /**
     * Get the opcode for a token
     *
     * @param opToken an ADDOP, MULOP, or UNARYMINUS token
     * @return opcode ("add", "mul", ...)
     */
    private String getOpCode(Token opToken) {
        String opcode = "";

        switch (opToken.getType()) {
            case ADDOP:
                int value = (int) opToken.getValue();
                if (value == 1)
                    opcode = "add";
                else if (value == 2)
                    opcode = "sub";
                break;
            case MULOP:
                switch ((int) opToken.getValue()) {
                    case 1:
                        opcode = "mul";
                        break;
                    // division operator (/)
                    case 2:
                        opcode = "div";
                        break;
                    // DIV keyword
                    case 3:
                        opcode = "DIV";
                        break;
                    case 4:
                        opcode = "MOD";
                        break;
                }
                break;
            case UNARYMINUS:
                opcode = "uminus";
        }

        return opcode;
    }

    /**
     * Gets the intermediate code from quads in a pretty format
     *
     * @return String representation of generated intermediate code
     */
    public String getInterCode() {
        StringBuilder out = new StringBuilder("CODE\n");

        for (int i = 1; i < quads.size(); i++) {
            String[] quad = quads.get(i);
            out.append(i).append(":  ").append(quad[0]);

            if (quad.length > 1)
                out.append(" ").append(quad[1]);

            for (int j = 2; j < quad.length; j++)
                out.append(", ").append(quad[j]);

            out.append("\n");
        }
        return out.toString();
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


    public Stack<Token> getTokenStack() {
        return tokenStack;
    }

    public Stack<SymbolTableEntry> getSteStack() {
        return steStack;
    }

}
