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
    private final Stack<Object> stack = new Stack<>();
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
            case 30:
                thirty(prevToken);
                break;
            case 31:
                thirtyOne(token);
                break;
            case 40:
                stack.push(prevToken);
                break;
            case 41:
                fourtyOne();
                break;
            case 42:
                fourtyTwo(prevToken);
                break;
            case 43:
                fourtyThree();
                break;
            case 44:
                stack.push(prevToken);
                break;
            case 45:
                fourtyFive();
                break;
            case 46:
                fourtySix(prevToken); // WHICH TOKEN whos to say?
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
        TokenType type = ((Token) stack.pop()).getType();
        SymbolTableEntry entry;
        int memorySize = 1;
        int upperBound = 0;
        int lowerBound = 0;
        // If we have an array figure out memory based on bounds
        // Otherwise its a variable so memory is 1
        if (array) {
            upperBound = Integer.parseInt(((Token) stack.pop()).getValue().toString());
            lowerBound = Integer.parseInt(((Token) stack.pop()).getValue().toString());
            memorySize = (upperBound - lowerBound) + 1;
        }

        while (!stack.isEmpty() && ((Token) stack.peek()).getType() == TokenType.IDENTIFIER) {
            String name = ((Token) stack.pop()).getValue().toString();
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
        stack.pop();
        stack.pop();
        Token id3 = (Token) stack.pop();

        SymbolTableEntry entry = new ProcedureEntry(id3.toString(), 0, new ArrayList<>());
        entry.setReserved(true);
        globalTable.insert(entry);
        insert = false;

        generate("call", "main", "0");
        generate("exit");
    }

    /**
     * Accesses a variable in a symbol table and adds it to the stack
     * @param token the token to look up
     * @throws SemanticError if the token was not found in a symbol table
     */
    private void thirty(Token token) throws SemanticError {
        SymbolTableEntry id = lookupId(token);
        if (id == null) {
            throw SemanticError.undeclaredVariable(token);
        }
        stack.push(id);
        stack.push(EType.ARITHMETIC);
    }

    /**
     * Variable assignment
     *
     * @param token to get row and column if an error occurs
     * @throws SymbolTableError
     * @throws SemanticError    if there is a type mismatch
     */
    private void thirtyOne(Token token) throws SymbolTableError, SemanticError {
        EType eType = (EType) stack.pop();

        if(stack.pop() != EType.ARITHMETIC){
            throw SemanticError.eTypeError(eType);
        }

        SymbolTableEntry id2 = (SymbolTableEntry) stack.pop();
        // offset will be implemented in later actions
        SymbolTableEntry offset = (SymbolTableEntry) stack.pop();
        SymbolTableEntry id1 = (SymbolTableEntry) stack.pop();

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
    private void fourtyOne() throws SymbolTableError, SemanticError {
        EType eType = (EType) stack.pop();

        if(stack.pop() != EType.ARITHMETIC){
            throw SemanticError.eTypeError(eType);
        }

        SymbolTableEntry id = (SymbolTableEntry) stack.pop();
        Token sign = (Token) stack.pop();
        if (sign.getType() == TokenType.UNARYMINUS) {
            VariableEntry temp = createTemp(id.getType());
            // Integer or float?
            if (id.getType() == TokenType.INTEGER) {
                generate("uminus", id, temp);
            } else {
                generate("fuminus", id, temp);
            }
            stack.push(temp);
        } else
            stack.push(id);

        stack.push(EType.ARITHMETIC);
    }

    private void fourtyTwo(Token token)  throws SemanticError{
        EType etype = (EType) stack.pop();

        if (token.getType() == TokenType.ADDOP && (int) token.getValue() == 1 ) { // TODO GUESS DO RIGHT FOR OR
            if (etype != EType.RELATIONAL)
                throw  SemanticError.eTypeError(etype);
            // the top of the stack should be a list of integers
            List<Integer> EFalse = (List<Integer>) stack.peek();
            backpatch(EFalse, quads.size()); // TODO write new backpatch
        }
        else {
            if (etype != EType.ARITHMETIC)
                throw  SemanticError.eTypeError(etype);
        }
        // until here /\ /\ /\

        // the token should be an operator
        stack.push(token);
    }

    /**
     * Handles arithmetic and comparison operations
     */
    private void fourtyThree() throws SymbolTableError {
        SymbolTableEntry id2 = (SymbolTableEntry) stack.pop();
        // this is one place where the operator from action 42 is popped
        Token operator = (Token) stack.pop();
        // get the TVI opcode associated with the operator token
        // ex. for a token representing addition, opcode would be "add"
        String opcode = getOpCode(operator);
        SymbolTableEntry id1 = (SymbolTableEntry) stack.pop();

        if (typeCheck(id1, id2) == 0) {
            VariableEntry temp = createTemp(TokenType.INTEGER);
            generate(opcode, id1, id2, temp);
            stack.push(temp);
        } else
           checkAdd(id1, id2, opcode);

    }

    /**
     * Evaluate arithmetic expressions
     *
     * @throws SymbolTableError
     * @throws SemanticError
     */
    private void fourtyFive() throws SymbolTableError, SemanticError {
        // Pushed in #46
        SymbolTableEntry id2 = (SymbolTableEntry) stack.pop();
        // Pushed in #44
        Token operator = (Token) stack.pop();
        // Pushed in #46
        String opcode = getOpCode(operator);
        SymbolTableEntry id1 = (SymbolTableEntry) stack.pop();

        if (typeCheck(id1, id2) != 0 && (opcode.equals("DIV") || opcode.equals("MOD"))) {
            // MOD and DIV require integer operands
            throw SemanticError.badParameter("Operands of the " + opcode.toLowerCase() +
                    " operator must both be integers", operator);
        }

        // Are id1 and id2 both integers?
        if (typeCheck(id1, id2) == 0) {
            // Handle MOD and DIV keywords
            if (opcode.equals("MOD")) {
                VariableEntry temp1 = createTemp(TokenType.INTEGER);
                VariableEntry temp2 = createTemp(TokenType.INTEGER);
                VariableEntry temp3 = createTemp(TokenType.INTEGER);
                generate("div", id1, id2, temp1);
                generate("mul", id2, temp1, temp2);
                generate("sub", id1, temp2, temp3);
                stack.push(temp3);
            } else if (opcode.equals("div")) { // div or DIV??
                VariableEntry temp1 = createTemp(TokenType.REAL);
                VariableEntry temp2 = createTemp(TokenType.REAL);
                VariableEntry temp3 = createTemp(TokenType.REAL);
                generate("ltof", id1, temp1);
                generate("ltof", id2, temp2);
                generate("fdiv", temp1, temp2, temp3);
                stack.push(temp3);
            } else {
                // Generate for 2 integers
                VariableEntry temp = createTemp(TokenType.INTEGER);
                generate(opcode, id1, id2, temp);
                stack.push(temp);
            }
        } else
            checkAdd(id1, id2, opcode);
    }


    /**
     * Helper function for actions 45 and 46, their final else case is the same code.
     * Performs an operation when either id1 or id2 is not an integer.
     * @throws SymbolTableError
     */
    private void checkAdd(SymbolTableEntry id1, SymbolTableEntry id2, String opcode) throws SymbolTableError{
        // id1 and id2 are both reals
        if (typeCheck(id1, id2) == 1) {
            VariableEntry temp = createTemp(TokenType.REAL);
            generate("f" + opcode, id1, id2, temp);
            stack.push(temp);
        } else { // id1 and id2 are different types of numbers
            VariableEntry temp1 = createTemp(TokenType.REAL);
            VariableEntry temp2 = createTemp(TokenType.REAL);
            generate("ltof", id2, temp1);
            generate("f" + opcode, id1, temp1, temp2);
            stack.push(temp2);
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

            stack.push(id);
        } else if (token.getType() == TokenType.INTCONSTANT || token.getType() == TokenType.REALCONSTANT) {
            // look for the token in the constant symbol table
            SymbolTableEntry id = constantTable.search(token.getValue().toString());
            // if token is not found
            if (id == null) {
                id = new ConstantEntry(token.getValue().toString(), token.getType());
                constantTable.insert(id);
            }
            stack.push(id);
        }

    }

    /**
     * Semantic action 48
     */
    private void fourtyEight() throws SymbolTableError {
        // offset will be implemented in later actions
        SymbolTableEntry offset = null;
        if (offset != null) {
            SymbolTableEntry id = (SymbolTableEntry) stack.pop();
            VariableEntry temp = createTemp(id.getType());
            generate("load", id, offset, temp);
            stack.push(temp);
        }
    }

    /**
     * Backpatches global memory to ensure the correct amount is allocated at the start.
     * Frees that much global memory at the end.
     */
    private void fiftyFive() {
        backpatch(globalStore, globalMemory);
        generate("free", Integer.toString(globalMemory));
        generate("PROCEND");
    }

    /**
     * Adds the first couple instructions
     */
    private void fiftySix() {
        generate("PROCBEGIN", "main");
        globalStore = quads.size();
        // the underscore as the second arguement in generate
        // is a placeholder that will be filled in later by backpatch
        generate("alloc", "_");
    }

    // Helper Methods

    /**
     * Gets the address of a symbol table entry
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

    /**
     * Gets the prefix of a symbol table entry
     * @param ste entry to get prefix for
     * @return '_' for global '%' for local
     */
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


    // Generate Methods, each of these generate a new quadruple (string array)
    // Many overloaded methods for the various parameters that generate could be called on

    /**
     * Universal generate method. Called by other generate methods.
     */
    private void generate(String tviCode, String[] operands) {
        String[] quadEntry = new String[operands.length + 1];
        // First entry is code
        quadEntry[0] = tviCode;

        // Copy array passed in to rest of quad entries
        System.arraycopy(operands, 0, quadEntry, 1, operands.length);

        quads.add(quadEntry);
    }

    private void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, SymbolTableEntry operand3) throws SymbolTableError {
        generate(tviCode, new String[]{steAddr(operand1), steAddr(operand2), steAddr(operand3)});
    }

    private void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2) throws SymbolTableError {
        generate(tviCode, new String[]{steAddr(operand1), steAddr(operand2)});
    }

    private void generate(String tviCode, String operand1) {
        generate(tviCode, new String[]{operand1});
    }

    private void generate(String tviCode, String operand1, String operand2) {
        generate(tviCode, new String[]{operand1, operand2});
    }

    private void generate(String tviCode) {
        generate(tviCode, new String[]{});
    }

    private void generate(String tviCode, String operand1, SymbolTableEntry operand2) throws SymbolTableError {
        generate(tviCode, new String[]{operand1, steAddr(operand2)});
    }

    /**
     * For a symbol table entry, returns a string for the local or global address
     * of that entry. Used by generate methods.
     */
    private String steAddr(SymbolTableEntry ste) throws SymbolTableError {
        return getSTEPrefix(ste) + getSTEAddress(ste);
    }

    /**
     * Creates a new variable entry and inserts it into the proper symbol table.
     *
     * @param type type of variable
     * @return the variable entry
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

        // Both integers
        if (int1 && int2)
            return 0;
        // Both reals
        else if (!int1 && !int2)
            return 1;
        // Different types
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
     * Used for when we don't know the address when the quad is
     * generated.
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

    public Stack<Object> getStack() {
        return stack;
    }


}
