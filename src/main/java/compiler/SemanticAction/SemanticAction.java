package compiler.SemanticAction;

import compiler.Exception.SemanticError;
import compiler.Exception.SymbolTableError;
import compiler.Lexer.Token;
import compiler.Lexer.TokenType;
import compiler.SymbolTable.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SemanticAction {
    private final SymbolTable globalTable = new SymbolTable(20);
    private final SymbolTable constantTable = new SymbolTable(20);
    private final SymbolTable localTable = new SymbolTable(20);
    private final Stack<Object> stack = new Stack<>();
    // List of quadruples, which we represent as string arrays
    private final ArrayList<String[]> quads = new ArrayList<>();
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
            SymbolTableEntry entry;
            for (String res : new String[]{"READ", "WRITE", "MAIN"}) {
                entry = new ProcedureEntry(res, 0, new ArrayList<>());
                entry.setReserved(true);
                globalTable.insert(entry);
            }

            for (String res : new String[]{"INPUT", "OUTPUT"}) {
                entry = new IODeviceEntry(res);
                entry.setReserved(true);
                globalTable.insert(entry);
            }
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
            case 22:
                twentyTwo(token);
                break;
            case 24:
                //Store line number of beginning of loop.
                stack.push(quads.size());
                break;
            case 25:
                twentyFive(token);
                break;
            case 26:
                twentySix();
                break;
            case 27:
                twentySeven();
                break;
            case 28:
                twentyEight();
                break;
            case 29:
                twentyNine();
                break;
            case 30:
                thirty(prevToken);
                break;
            case 31:
                thirtyOne(prevToken);
                break;
            case 32:
                thirtyTwo(token);
                break;
            case 33:
                thirtyThree(prevToken);
                break;
            case 34:
                thirtyFour(prevToken);
                break;
            case 38:
                thirtyEight(prevToken);
                break;
            case 39:
                thirtyNine(prevToken);
                break;
            case 40:
                stack.push(prevToken);
                break;
            case 41:
                fourtyOne(token);
                break;
            case 42:
                fourtyTwo(prevToken);
                break;
            case 43:
                fourtyThree();
                break;
            case 44:
                fourtyFour(prevToken);
                break;
            case 45:
                fourtyFive();
                break;
            case 46:
                fourtySix(prevToken);
                break;
            case 47:
                fourtySeven(token);
                break;
            case 48:
                fourtyEight(prevToken);
                break;
            case 53:
                fiftyThree();
                break;
            case 54:
                fiftyFour();
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
     * Update branch destination for IF -> #t to next quad.
     */
    private void twentyTwo(Token token) throws SemanticError {
        EType etype = (EType) stack.pop();
        if (etype != EType.RELATIONAL)
            throw SemanticError.eTypeError(etype, token);

        // Always casting to generic List instead of List<Integer> to avoid unchecked cast warnings
        List EFalse = (List) stack.pop();
        List ETrue = (List) stack.pop();
        // Backpatch destinations in ETRUE using the number of the next quad (where the TRUE case is).
        backpatch(ETrue, quads.size());
        stack.push(ETrue);
        stack.push(EFalse);
    }

    /**
     * Initialisation for a WHILE loop.
     * This code is the same as action 22, but it happens in the case of a while loop instead of an if.
     */
    private void twentyFive(Token token) throws SemanticError {
        twentyTwo(token); // They're exactly the same??
    }

    /**
     * Write code at end of WHILE loop.
     */
    private void twentySix() {
        List EFalse = (List) stack.pop();
        stack.pop();
        // beginLoop is pushed onto the stack in action 24. It's the start of the loop
        generate("goto", Integer.toString((int) stack.pop()));
        // Go to the next line when the condition fails
        backpatch(EFalse, quads.size());
    }

    /**
     * Sets up else statement.
     */
    private void twentySeven() {
        // skipElse is the first line of the else code
        List skipElse = Collections.singletonList(quads.size());
        generate("goto", "_");
        List EFalse = (List) stack.pop();
        List ETrue = (List) stack.pop();
        // Backpatch EFalse with the line number of skipElse
        backpatch(EFalse, quads.size());
        stack.push(skipElse);
        stack.push(ETrue);
        stack.push(EFalse);
    }

    /**
     * End of else statement.
     */
    private void twentyEight() {
        stack.pop();
        stack.pop();
        // skipElse is pushed onto the stack in action 27
        List skipElse = (List) stack.pop();
        // Backpatch where we go after else case
        backpatch(skipElse, quads.size());
    }

    /**
     * End of if without else.
     */
    private void twentyNine() {
        List EFalse = (List) stack.pop();
        stack.pop();
        // There is no else case so the false case is just the next line
        backpatch(EFalse, quads.size());
    }

    /**
     * Check to see if a variable has been declared
     *
     * @param token identifier variable
     * @throws SemanticError if the variable has not been declared
     */
    private void thirty(Token token) throws SemanticError {
        SymbolTableEntry id = lookupId(token);
        if (id == null)
            throw SemanticError.undeclaredVariable(token);

        stack.push(id);
        stack.push(EType.ARITHMETIC);
    }

    /**
     * Variable assignment
     *
     * @param token to get row and column if an error occurs
     * @throws SemanticError if there is a type mismatch
     */
    private void thirtyOne(Token token) throws SymbolTableError, SemanticError {
        SymbolTableEntry id2 = checkEType(token);
        // offset will be implemented in later actions
        SymbolTableEntry offset = (SymbolTableEntry) stack.pop();
        SymbolTableEntry id1 = (SymbolTableEntry) stack.pop();

        // We'll put the value of id2 in id1
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
     * Ensures top of stack is an array
     */
    private void thirtyTwo(Token token) throws SemanticError {
        EType etype = (EType) stack.pop();
        SymbolTableEntry id = (SymbolTableEntry) stack.peek();
        if (etype != EType.ARITHMETIC)
            throw SemanticError.eTypeError(etype, token);

        if (!id.isArray())
            throw SemanticError.idIsNotArray(id, token);

    }

    /**
     * Set up array offset
     */
    private void thirtyThree(Token token) throws SemanticError, SymbolTableError {
        EType etype = (EType) stack.pop();
        if (etype != EType.ARITHMETIC)
            throw SemanticError.eTypeError(etype, token);

        SymbolTableEntry id = (SymbolTableEntry) stack.pop();
        TokenType type = id.getType();
        if (type != TokenType.INTEGER && type != TokenType.INTCONSTANT)
            throw SemanticError.typeMismatch("Integer", type.toString(), token.getRow(), token.getCol());

        ArrayEntry array = (ArrayEntry) stack.peek();
        // Lower bound of the array, lowest index might not be 0
        VariableEntry temp1 = createTemp(TokenType.INTEGER);
        // Memory offset
        VariableEntry temp2 = createTemp(TokenType.INTEGER);
        generate("move", Integer.toString(array.getLowBound()), temp1);
        generate("sub", id, temp1, temp2);
        stack.push(temp2);
    }

    /**
     * Function or procedure
     */
    private void thirtyFour(Token token) throws SemanticError, SymbolTableError {
        EType etype = (EType) stack.pop();
        SymbolTableEntry id = (SymbolTableEntry) stack.peek();
        if (id.isFunction()) {
            stack.push(etype);
            execute(52, null, token);
        } else
            stack.push(null);
    }

    /**
     * Ensure arithmetic operation
     */
    private void thirtyEight(Token token) throws SemanticError {
        EType etype = (EType) stack.pop();
        if (etype != EType.ARITHMETIC)
            throw SemanticError.eTypeError(etype, token);
        // token should be an operator
        stack.push(token);
    }

    /**
     * Handle RelOp expressions
     */
    private void thirtyNine(Token token) throws SemanticError, SymbolTableError {
        EType etype = (EType) stack.pop();
        // Ensure it is a arithop
        if (etype != EType.ARITHMETIC)
            throw SemanticError.eTypeError(etype, token);

        SymbolTableEntry id2 = (SymbolTableEntry) stack.pop();
        Token operator = (Token) stack.pop();
        // the operator must be replaced with the proper TVI code which
        // jump if the condition is meant
        String opcode = getOpCode(operator);
        SymbolTableEntry id1 = (SymbolTableEntry) stack.pop();
        // Generate appropriate code based on types of ids
        if (typeCheck(id1, id2) == 2) {
            VariableEntry temp = createTemp(TokenType.REAL);
            generate("ltof", id2, temp);
            generate(opcode, id1, temp, "_");
        } else if (typeCheck(id1, id2) == 3) {
            VariableEntry temp = createTemp(TokenType.REAL);
            generate("ltof", id1, temp);
            generate(opcode, temp, id2, "_");
        } else
            generate(opcode, id1, id2, "_");

        generate("goto", "_");
        // Addresses to backpatch in #22
        List ETrue = Collections.singletonList(quads.size() - 2);
        List EFalse = Collections.singletonList(quads.size() - 1);
        stack.push(ETrue);
        stack.push(EFalse);
        stack.push(EType.RELATIONAL);
    }

    /**
     * Evaluate unary operators
     */
    private void fourtyOne(Token token) throws SymbolTableError, SemanticError {
        SymbolTableEntry id = checkEType(token);
        Token sign = (Token) stack.pop();

        // If the operator is uminus, create a temp var to store the result
        if (sign.getType() == TokenType.UNARYMINUS) {
            TokenType type = id.getType();
            VariableEntry temp = createTemp(type);
            // Integer or float?
            if (id.getType() == TokenType.INTEGER || id.getType() == TokenType.INTCONSTANT)
                generate("uminus", id, temp);
            else
                generate("fuminus", id, temp);

            stack.push(temp);
        } else
            stack.push(id);

        stack.push(EType.ARITHMETIC);
    }

    /**
     * Helper function for actions 31 and 41 to avoid duplicate code
     *
     * @return Symbol table entry from stack if EType.ARITHMETIC was on stack before
     * @throws SemanticError if top of stack is not EType.ARITHMETIC
     */
    private SymbolTableEntry checkEType(Token token) throws SemanticError {
        EType eType = (EType) stack.pop();

        if (eType != EType.ARITHMETIC)
            throw SemanticError.eTypeError(eType, token);

        return (SymbolTableEntry) stack.pop();
    }

    /**
     * Backpatches or blocks
     */
    private void fourtyTwo(Token token) throws SemanticError {
        EType etype = (EType) stack.pop();

        if (getOpCode(token).equals("or")) {
            if (etype != EType.RELATIONAL)
                throw SemanticError.eTypeError(etype, token);
            // the top of the stack should be a list of integers
            List EFalse = (List) stack.peek();
            backpatch(EFalse, quads.size());
        } else {
            if (etype != EType.ARITHMETIC)
                throw SemanticError.eTypeError(etype, token);
        }

        // the token should be an operator
        stack.push(token);
    }

    /**
     * Evaluate addition, subtraction, and OR
     */
    private void fourtyThree() throws SymbolTableError {
        EType etype = (EType) stack.pop();
        if (etype == EType.RELATIONAL) {
            List E2False = (List) stack.pop();
            List E2True = (List) stack.pop();
            stack.pop();
            stack.pop();
            List E1True = (List) stack.pop();

            List ETrue = merge(E1True, E2True);
            stack.push(ETrue);
            stack.push(E2False);
            stack.push(EType.RELATIONAL);
        } else { // if etype == EType.ARITHMETIC

            SymbolTableEntry id2 = (SymbolTableEntry) stack.pop();
            // this is one place where the operator from action 42 is popped
            Token operator = (Token) stack.pop();
            // get the TVI opcode associated with the operator token
            // ex. for a token representing addition, opcode would be "add"
            String opcode = getOpCode(operator);
            SymbolTableEntry id1 = (SymbolTableEntry) stack.pop();

            // Both integers?
            if (typeCheck(id1, id2) == 0) {
                VariableEntry temp = createTemp(TokenType.INTEGER);
                generate(opcode, id1, id2, temp);
                stack.push(temp);
            } else
                checkAdd(id1, id2, opcode);
            stack.push(EType.ARITHMETIC);
        }
    }

    /**
     * Backpatches 'and' blocks
     */
    private void fourtyFour(Token token) {
        if (stack.pop() == EType.RELATIONAL) {
            List EFalse = (List) stack.pop();
            List ETrue = (List) stack.pop();
            // and is represented by a series of 'beq_,_,_,  goto' statements, the beq goes to where we want if each
            // boolean in the 'and' is true, the goto jump somewhere if any boolean in the 'and' is false
            // Here, we want to backpatch ETrue to 2 addresses forward because we know there'll be a goto directly following
            if (getOpCode(token).equals("and"))
                backpatch(ETrue, quads.size());

            stack.push(ETrue);
            stack.push(EFalse);
        }

        stack.push(token);
    }

    /**
     * Evaluate multiplication, division, modular arithmetic, and AND
     *
     */
    private void fourtyFive() throws SymbolTableError, SemanticError {
        EType etype = (EType) stack.pop();
        if (etype == EType.RELATIONAL) {
            List E2False = (List) stack.pop();
            List E2True = (List) stack.pop();
            Token operator = (Token) stack.pop();

            if (getOpCode(operator).equals("and")) {
                List E1False = (List) stack.pop();
                stack.pop();

                List EFalse = merge(E1False, E2False);
                stack.push(E2True);
                stack.push(EFalse);
                stack.push(EType.RELATIONAL);
            }
        } else {
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
            stack.push(EType.ARITHMETIC);
        }
    }

    /**
     * Helper function for actions 45 and 46, their final else case is the same code.
     * Performs an operation when either id1 or id2 is not an integer.
     *
     */
    private void checkAdd(SymbolTableEntry id1, SymbolTableEntry id2, String opcode) throws SymbolTableError {
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
     * @param token identifier or constant
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
            // if not found add it to the constant table
            if (id == null) {
                id = new ConstantEntry(token.getValue().toString(), token.getType());
                constantTable.insert(id);
            }
            stack.push(id);
        }
        stack.push(EType.ARITHMETIC);
    }

    /**
     * Handles NOT reserved word
     */
    private void fourtySeven(Token token) throws SemanticError {
        EType etype = (EType) stack.pop();
        if (etype != EType.RELATIONAL)
            throw SemanticError.eTypeError(etype, token);

        // swap ETrue and EFalse on the stack
        List EFalse = (List) stack.pop();
        List ETrue = (List) stack.pop();
        stack.push(EFalse);
        stack.push(ETrue);
        stack.push(EType.RELATIONAL);
    }


    /**
     * Array lookup
     */
    private void fourtyEight(Token token) throws SymbolTableError, SemanticError {
        SymbolTableEntry offset = (SymbolTableEntry) stack.pop();
        // offset will be implemented in later actions
        if (offset != null) {
            if (offset.isFunction()) { // <- add this if statement
                // call action 52 with the token from the parser
                execute(52, null, token);
            }
            SymbolTableEntry id = (SymbolTableEntry) stack.pop();
            VariableEntry temp = createTemp(id.getType());
            generate("load", id, offset, temp);
            stack.push(temp);
        }
        stack.push(EType.ARITHMETIC);
    }

    /**
     * Lookup variable or function result
     */
    private void fiftyThree() {
        EType etype = (EType) stack.pop();
        SymbolTableEntry id = (SymbolTableEntry) stack.pop();
        if (id.isFunction()) {
           /* // this will be added in the final phase
           if (id != currentFunction) {
               throw illegal procedure error
           }
           stack.push(id.getResult());
           stack.push(EType.ARITHMETIC);
           */
        } else {
            stack.push(id);
            stack.push(etype);
        }
    }

    /**
     * Confirm statement is a procedure call
     */
    private void fiftyFour() throws SemanticError {
        EType etype = (EType) stack.pop();
        SymbolTableEntry id = (SymbolTableEntry) stack.pop();
        if (!id.isProcedure()) {
            throw SemanticError.illegalProcedure(id);
        }
        stack.push(etype);
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
     *
     * @param ste entry to get prefix for
     * @return '_' for global '%' for local
     */
    private String getSTEPrefix(SymbolTableEntry ste) {
        if (global)
            return "_";
        else { // local
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
    private void generate(String... operands) {
        String[] quadEntry = new String[operands.length];
        // First entry is code
        quadEntry[0] = operands[0];

        // Copy array passed in to rest of quad entries
        System.arraycopy(operands, 1, quadEntry, 1, operands.length-1);

        quads.add(quadEntry);
    }

    private void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, SymbolTableEntry
            operand3) throws SymbolTableError {
        generate(tviCode, steAddr(operand1), steAddr(operand2), steAddr(operand3));
    }

    private void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, String operand3) throws SymbolTableError {
        generate(tviCode, steAddr(operand1), steAddr(operand2), operand3);
    }

    private void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2) throws
            SymbolTableError {
        generate(tviCode, steAddr(operand1), steAddr(operand2));
    }

    private void generate(String tviCode, String operand1, SymbolTableEntry operand2) throws SymbolTableError {
        generate(tviCode, operand1, steAddr(operand2));
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

    private void backpatch(List list, int x) {
        for (Object val : list) {
            // Eliminates "Unchecked type warning"
            Integer i = (Integer) val;
            if (quads.get(i)[0].equals("goto"))
                quads.get(i)[1] = Integer.toString(x);
            else// quad is a branch statement
                quads.get(i)[3] = Integer.toString(x);
        }
    }

    /**
     * Get the opcode for a token
     *
     * @param opToken an ADDOP, MULOP, or UNARYMINUS token
     * @return opcode ("add", "mul", ...)
     */
    private String getOpCode(Token opToken) {
        switch (opToken.getType()) {
            case ADDOP:
                switch ((int) opToken.getValue()) {
                    case 1:
                        return "add";
                    case 2:
                        return "sub";
                    case 3:
                        return "or";
                }
                break;
            case MULOP:
                switch ((int) opToken.getValue()) {
                    case 1:
                        return "mul";
                    // division operator (/)
                    case 2:
                        return "div";
                    // DIV keyword
                    case 3:
                        return "DIV";
                    case 4:
                        return "MOD";
                    case 5:
                        return "and";
                }
                break;
            case UNARYMINUS:
                return "uminus";
            case RELOP:
                switch ((int) opToken.getValue()) {
                    case 1:
                        return "beq";
                    case 2:
                        return "bne";
                    case 3:
                        return "blt";
                    case 4:
                        return "bgt";
                    case 5:
                        return "ble";
                    case 6:
                        return "bge";
                }
                break;
        }
        return " ";
    }

    private List merge(List l1, List l2){
        // Going to some length to avoid an unchecked type warning, ensuring type safety
        return Stream.of(l1.toArray(), l2.toArray()).flatMap(Stream::of).collect(Collectors.toList());
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
