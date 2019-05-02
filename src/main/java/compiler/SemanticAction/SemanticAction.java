package compiler.SemanticAction;

import compiler.Exception.GenSemanticErr;
import compiler.Exception.SemanticError;
import compiler.Exception.SymbolTableError;
import compiler.Lexer.Token;
import compiler.Lexer.TokenType;
import compiler.SymbolTable.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SemanticAction
{
    private final SymbolTable globalTable = new SymbolTable();
    private final SymbolTable constantTable = new SymbolTable();
    private final Stack<Object> stack = new Stack<>();
    // We can push a list of [ SymbolTableEntries OR any of its subtypes ] to this stack
    private final Stack<List<? extends SymbolTableEntry>> paramStack = new Stack<>();
    // List of quadruples, which we represent as string arrays
    private final ArrayList<String[]> quads = new ArrayList<>();
    private Stack<Integer> paramCount = new Stack<>();
    private SymbolTable localTable = new SymbolTable();
    private FPEntry currentFunction;
    private boolean global = true;
    // --Commented out by Inspection (2019-04-16 11:10):private boolean insert = true;
    private boolean array = false;
    private int nextParam;
    private int localStore = 0;
    private int globalStore = 0;
    private int globalMemory = 0;
    private int localMemory = 0;
    private int tempCt = -1; //TODO -1 for testing purposes, change to 0
    private GenSemanticErr SemErr = new GenSemanticErr();

    /**
     * Constructor, insert default entries into global table
     */
    public SemanticAction()
    {
        // Add dummy quad
        quads.add(new String[]{null, null, null, null});
        // Insert reserved words into the global table
        try
        {
            SymbolTableEntry entry;
            for (String res : new String[]{"READ", "WRITE", "MAIN"})
            {
                entry = new ProcedureEntry(res, 0, new ArrayList<>());
                entry.setReserved(true);
                globalTable.insert(entry);
            }

            for (String res : new String[]{"INPUT", "OUTPUT"})
            {
                entry = new IODeviceEntry(res);
                entry.setReserved(true);
                globalTable.insert(entry);
            }
        } catch (SymbolTableError e)
        {
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
    public void execute(int num, Token token, Token prevToken) throws SemanticError, SymbolTableError
    {
        switch (num)
        {
            case 3:
                three();
                break;
            case 4:
                stack.push(prevToken);
                break;
            case 5:
                five();
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
            case 11:
                eleven();
                break;
            case 13:
                stack.push(prevToken);
                break;
            case 15:
                fifteen(prevToken);
                break;
            case 16:
                sixteen();
                break;
            case 17:
                seventeen(prevToken);
                break;
            case 19:
                nineteen();
                break;
            case 20:
                twenty();
                break;
            case 21:
                twentyOne();
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
            case 35:
                thirtyFive();
                break;
            case 36:
                thirtySix(prevToken);
                break;
            case 37:
                thirtySeven(prevToken);
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
            case 49:
                fourtyNine(prevToken);
                break;
            case 50:
                fifty(prevToken);
                break;
            case 51:
                fiftyOne(prevToken);
                break;
            case 52:
                fiftyTwo(prevToken);
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
            case 57:
                fiftyOneR();
                break;
            case 58:
                fiftyOneW();
                break;
        }
    }

    // Semantic Actions

    /**
     * Semantic action 3, handles declarations of arrays and variables
     *
     * @throws SymbolTableError if an id is added to the local or global symbol table that already exists
     */
    private void three() throws SymbolTableError
    {
        TokenType type = ((Token) stack.pop()).getType();
        SymbolTableEntry entry;
        int memorySize = 1;
        int upperBound = 0;
        int lowerBound = 0;
        // If we have an array figure out memory based on bounds
        // Otherwise its a variable so memory is 1
        if (array)
        {
            upperBound = Integer.parseInt(((Token) stack.pop()).getValue().toString());
            lowerBound = Integer.parseInt(((Token) stack.pop()).getValue().toString());
            memorySize = (upperBound - lowerBound) + 1;
        }

        while (!stack.isEmpty() && (stack.peek() instanceof Token && (((Token) stack.peek()).getType() == TokenType.IDENTIFIER)))
        {
            String name = ((Token) stack.pop()).getValue().toString();
            // Create array or variable entry
            entry = array
                    ? new ArrayEntry(name, type, upperBound, lowerBound)
                    : new VariableEntry(name, type);

            // Add to local or global symbol table
            if (global)
            {
                entry.setAddress(-1 * globalMemory);
                globalTable.insert(entry);
                globalMemory += memorySize;
            } else
            {
                entry.setAddress(localMemory);
                localTable.insert(entry);
                localMemory += memorySize;
            }
        }
        array = false;
    }

    /**
     * Generate code for start of function.
     */
    private void five()
    {
        SymbolTableEntry id = (SymbolTableEntry) stack.pop();
        generate("PROCBEGIN", id.getName());
        localStore = quads.size();
        // Will be backpatched
        generate("alloc", "_");
    }

    /**
     * Semantic action 9, adds the name of the program to the global table
     */
    private void nine() throws SymbolTableError
    {
        stack.pop();
        stack.pop();
        Token id3 = (Token) stack.pop();

        SymbolTableEntry entry = new ProcedureEntry(id3.toString(), 0, new ArrayList<>());
        entry.setReserved(true);
        globalTable.insert(entry);

        generate("call", "main", "0");
        generate("exit");
    }

    /**
     * Generate code for end of function.
     */
    private void eleven()
    {
        global = true;
        // delete the local symbol table
        localTable = new SymbolTable();
        currentFunction = null;
        // Backpatch allocation for local memory
        backpatch(localStore, localMemory);
        generate("free", Integer.toString(localMemory));
        generate("PROCEND");
    }

    /**
     * Creates function name
     */
    private void fifteen(Token token) throws SymbolTableError
    {
        // create a variable to store the result of the function
        VariableEntry result = create(token.getValue() + "_RESULT", TokenType.INTEGER);
        // create a new function entry with name from the token
        // from the parser and the result variable just created
        FPEntry id = new FunctionEntry(token.getValue().toString(), result);
        globalTable.insert(id);
        global = false;
        localMemory = 0;
        currentFunction = id;
        stack.push(id);
    }

    /**
     * sets the type of the function and its result
     */
    private void sixteen()
    {
        Token type = (Token) stack.pop();
        FunctionEntry id = (FunctionEntry) stack.peek();
        id.setType(type.getType());
        // set the type of the result variable of id
        id.setResultType(type.getType());
        currentFunction = id;
    }

    /**
     * Create procedure in symbol table.
     */
    private void seventeen(Token token) throws SymbolTableError
    {
        // create a new procedure entry with the name of the token
        // from the parser
        FPEntry id = new ProcedureEntry(token.getValue().toString());
        globalTable.insert(id);
        global = false;
        localMemory = 0;
        currentFunction = id;
        stack.push(id);
    }

    /**
     * Initialise count of formal parameters.
     */
    private void nineteen()
    {
        paramCount = new Stack<>();
        paramCount.push(0);
    }

    /**
     * Get number of parameters.
     */
    private void twenty()
    {
        FPEntry id = (FPEntry) stack.peek();
        int numParams = paramCount.pop();
        // id is a function entry or a procedure entry
        id.setParams(numParams);
    }

    /**
     * Create temporary variables to store parameter info.
     */
    private void twentyOne() throws SymbolTableError
    {
        Token type = (Token) stack.pop();

        // if array, then pop the upper and lower bounds
        int upperBound = -1;
        int lowerBound = -1;
        if (array)
        {
            upperBound = Integer.parseInt(((Token) stack.pop()).getValue().toString());
            lowerBound = Integer.parseInt(((Token) stack.pop()).getValue().toString());
        }

        // the tokens on the stack, which represent parameters,
        // must be added from the bottom-most id to the top-most
        Stack<Token> parameters = new Stack<>();

        // as the ids are popped off the stack, push them onto to
        // the new stack to reverse the order
        Object top = stack.peek();
        while (top instanceof Token && ((Token) top).getType() == TokenType.IDENTIFIER)
        {
            parameters.push((Token) stack.pop());
            top = stack.peek();
        }

        while (!parameters.empty())
        {
            Token param = parameters.pop();
            AVEntry var;
            if (array)
                var = new ArrayEntry(param.getValue().toString(), localMemory,
                        type.getType(), upperBound, lowerBound);
            else
                var = new VariableEntry(param.getValue().toString(), localMemory, type.getType());

            var.setParameter();
            localTable.insert(var);
            // current function is either a procedure or function entry
            currentFunction.addParameter(var);
            localMemory++;
            // increment the top of paramCount
            paramCount.push(paramCount.pop() + 1);
        }
        array = false;

    }

    /**
     * Update branch destination for IF -> #t to next quad.
     */
    private void twentyTwo(Token token) throws SemanticError
    {
        EType etype = (EType) stack.pop();
        if (etype != EType.RELATIONAL)
            throw SemErr.eTypeError(etype, token);

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
    private void twentyFive(Token token) throws SemanticError
    {
        twentyTwo(token); // They're exactly the same??
    }

    /**
     * Write code at end of WHILE loop.
     */
    private void twentySix()
    {
        List EFalse = (List) stack.pop();
        stack.pop();
        // beginLoop is pushed onto the stack in action 24. It's the start of the loop
        generate("goto", Integer.toString((int) stack.pop()));
        // Go to the next line when the condition fails
        backpatch(EFalse, quads.size());
    }

    /**
     * Handles else statement.
     */
    private void twentySeven()
    {
        // Where to go after else is the next generated code
        // skipElse is the first line of the else code
        List skipElse = Collections.singletonList(quads.size());
        generate("goto", "_"); // Here it is!
        List EFalse = (List) stack.pop();
        List ETrue = (List) stack.pop();
        // Backpatch EFalse with the line number of skipElse
        backpatch(EFalse, quads.size());
        stack.push(skipElse);
        // Put ETrue and EFalse back on the stack
        stack.push(ETrue);
        stack.push(EFalse);
    }

    /**
     * End of else statement.
     */
    private void twentyEight()
    {
        stack.pop();
        stack.pop();
        // skipElse is pushed onto the stack in action 27. Skip else is where we go after the else.
        List skipElse = (List) stack.pop();
        // Backpatch where we go after else case
        backpatch(skipElse, quads.size());
    }

    /**
     * End of if without else.
     */
    private void twentyNine()
    {
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
    private void thirty(Token token) throws SemanticError
    {
        SymbolTableEntry id = lookupId(token);
        if (id == null)
            throw SemErr.undeclaredVariable(token);

        stack.push(id);
        stack.push(EType.ARITHMETIC);
    }

    /**
     * Variable assignment
     *
     * @param token to get row and column if an error occurs
     * @throws SemanticError if there is a type mismatch
     */
    private void thirtyOne(Token token) throws SymbolTableError, SemanticError
    {
        SymbolTableEntry id2 = checkEType(token);
        // offset will be implemented in later actions
        SymbolTableEntry offset = (SymbolTableEntry) stack.pop();
        SymbolTableEntry id1 = (SymbolTableEntry) stack.pop();

        // We'll put the value of id2 in id1
        if (typeCheck(id1, id2) == 3)
            throw SemErr.typeMismatch("Integer", "Real", token);

        if (typeCheck(id1, id2) == 2)
        {
            VariableEntry temp = createTemp(TokenType.REAL);
            generate("ltof", id2, temp);
            if (offset == null)
                generate("move", temp, id1);
            else
                generate("stor", temp, offset, id1);
        } else
        {
            if (offset == null)
                generate("move", id2, id1);
            else
                generate("stor", id2, offset, id1);
        }
    }

    /**
     * Ensures top of stack is an array
     */
    private void thirtyTwo(Token token) throws SemanticError
    {
        EType etype = (EType) stack.pop();
        SymbolTableEntry id = (SymbolTableEntry) stack.peek();
        if (etype != EType.ARITHMETIC)
            throw SemErr.eTypeError(etype, token);

        if (!id.isArray())
            throw SemErr.idIsNotArray(id, token);

    }

    /**
     * Set up array offset
     */
    private void thirtyThree(Token token) throws SemanticError, SymbolTableError
    {
        EType etype = (EType) stack.pop(); // Should be arith
        if (etype != EType.ARITHMETIC)
            throw SemErr.eTypeError(etype, token);

        SymbolTableEntry id = (SymbolTableEntry) stack.pop();
        TokenType type = id.getType();
        if (type != TokenType.INTEGER && type != TokenType.INTCONSTANT)
            throw SemErr.typeMismatch("Integer", type.toString(), token);

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
    private void thirtyFour(Token token) throws SemanticError, SymbolTableError
    {
        EType etype = (EType) stack.pop();
        SymbolTableEntry id = (SymbolTableEntry) stack.peek();
        if (id.isFunction())
        {
            stack.push(etype);
            execute(52, null, token);
        } else
            stack.push(null);
    }

    /**
     * Set up to call a procedure
     */
    private void thirtyFive()
    {
        EType etype = (EType) stack.pop();
        // id is a procedure entry
        FPEntry id = (ProcedureEntry) stack.peek();
        stack.push(etype);
        paramCount.push(0);
        paramStack.push(id.getParamInfo());
    }

    /**
     * Generate code to call a procedure.
     */
    private void thirtySix(Token token) throws SemanticError
    {
        stack.pop();
        ProcedureEntry id = (ProcedureEntry) stack.pop();
        if (id.getParams() != 0)
        {
            throw SemErr.badNumberParams(id, 0, id.getParams(), token);
        }
        generate("call", id.getName(), "0");
    }

    /**
     * Consume actual parameters in a list of parameters
     */
    private void thirtySeven(Token token) throws SemanticError
    {
        EType etype = (EType) stack.pop();
        if (etype != EType.ARITHMETIC)
        {
            throw SemErr.eTypeError(etype, token);
        }

        SymbolTableEntry id = (SymbolTableEntry) stack.peek();
        if (id.isProcedure() || id.isFunction())
        {
            throw SemErr.badParameterType(currentFunction, token);
        }

        // increment the top of paramCount
        paramCount.push(paramCount.pop() + 1);

        // find the name of the procedure/function on the bottom of the stack
        Stack<Object> parameters = new Stack<>();
        // Add parameters to temp stack until we hit a function or procedure
        while (!(stack.peek() instanceof SymbolTableEntry) || !(((SymbolTableEntry) stack.peek()).isFunction() || ((SymbolTableEntry) stack.peek()).isProcedure()))
        {
            parameters.push(stack.pop());
        }

        // funcId is a procedure or function entry
        FPEntry funcId;
        if (stack.peek() instanceof EType)
        {
            EType type = (EType) stack.pop();
            funcId = (FPEntry) stack.peek();
            stack.push(type);
        } else
            funcId = (FPEntry) stack.peek();

        // Add parameters to stack (now in correct order)
        while (!parameters.empty())
        {
            stack.push(parameters.pop());
        }

        String name = funcId.getName();
        if (!(name.equals("READ") || name.equals("WRITE")))
        {
            if (paramCount.peek() > funcId.getParams())
            {
                throw SemErr.badNumberParams(funcId, funcId.getParams(), paramCount.peek(), token);
            }
            SymbolTableEntry param = paramStack.peek().get(nextParam);
            TokenType retType = param.getType();
            if (id.getType() != retType && !((retType == TokenType.INTEGER && id.getType() == TokenType.INTCONSTANT) || (retType == TokenType.REAL && id.getType() == TokenType.REALCONSTANT)))
            {
                throw SemErr.badParameterType(funcId, id, param, token);
            }
            if (param.isArray())
            {
                ArrayEntry paramArr = (ArrayEntry) param;
                ArrayEntry idArr = (ArrayEntry) id;
                if ((idArr.getLowBound() != paramArr.getLowBound()) || (idArr.getUpBound() != paramArr.getUpBound()))
                {
                    throw SemErr.badParameterType(funcId, id, param, token);
                }
            }
            nextParam++;
        }

    }

    /**
     * Ensure arithmetic operation
     */
    private void thirtyEight(Token token) throws SemanticError
    {
        EType etype = (EType) stack.pop();
        if (etype != EType.ARITHMETIC)
            throw SemErr.eTypeError(etype, token);
        // token should be an operator
        stack.push(token);
    }

    /**
     * Handle RelOp expressions
     */
    private void thirtyNine(Token token) throws SemanticError, SymbolTableError
    {
        EType etype = (EType) stack.pop();
        // Ensure it is a arithop
        if (etype != EType.ARITHMETIC)
            throw SemErr.eTypeError(etype, token);

        SymbolTableEntry id2 = (SymbolTableEntry) stack.pop();
        Token operator = (Token) stack.pop();
        // the operator must be replaced with the proper TVI code which
        // jump if the condition is meant
        String opcode = getOpCode(operator);
        SymbolTableEntry id1 = (SymbolTableEntry) stack.pop();
        // Generate appropriate code based on types of ids
        if (typeCheck(id1, id2) == 2)
        {
            VariableEntry temp = createTemp(TokenType.REAL);
            generate("ltof", id2, temp);
            generate(opcode, id1, temp, "_");
        } else if (typeCheck(id1, id2) == 3)
        {
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
    private void fourtyOne(Token token) throws SymbolTableError, SemanticError
    {
        SymbolTableEntry id = checkEType(token);
        Token sign = (Token) stack.pop();

        // If the operator is uminus, create a temp var to store the result
        if (sign.getType() == TokenType.UNARYMINUS)
        {
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
    private SymbolTableEntry checkEType(Token token) throws SemanticError
    {
        EType eType = (EType) stack.pop();

        if (eType != EType.ARITHMETIC)
            throw SemErr.eTypeError(eType, token);

        return (SymbolTableEntry) stack.pop();
    }

    /**
     * Backpatches or blocks
     */
    private void fourtyTwo(Token token) throws SemanticError
    {
        EType etype = (EType) stack.pop();

        if (getOpCode(token).equals("or"))
        {
            if (etype != EType.RELATIONAL)
                throw SemErr.eTypeError(etype, token);
            // the top of the stack should be a list of integers
            List EFalse = (List) stack.peek();
            backpatch(EFalse, quads.size());
        } else
        {
            if (etype != EType.ARITHMETIC)
                throw SemErr.eTypeError(etype, token);
        }

        // the token should be an operator
        stack.push(token);
    }

    /**
     * Evaluate addition, subtraction, and OR
     */
    private void fourtyThree() throws SymbolTableError
    {
        EType etype = (EType) stack.pop();
        if (etype == EType.RELATIONAL)
        {
            List E2False = (List) stack.pop();
            List E2True = (List) stack.pop();
            stack.pop();
            stack.pop();
            List E1True = (List) stack.pop();

            List ETrue = merge(E1True, E2True);
            stack.push(ETrue);
            stack.push(E2False);
            stack.push(EType.RELATIONAL);
        } else
        { // if etype == EType.ARITHMETIC

            SymbolTableEntry id2 = (SymbolTableEntry) stack.pop();
            // this is one place where the operator from action 42 is popped
            Token operator = (Token) stack.pop();
            // get the TVI opcode associated with the operator token
            // ex. for a token representing addition, opcode would be "add"
            String opcode = getOpCode(operator);
            SymbolTableEntry id1 = (SymbolTableEntry) stack.pop();

            // Both integers?
            if (typeCheck(id1, id2) == 0)
            {
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
    private void fourtyFour(Token token)
    {
        if (stack.pop() == EType.RELATIONAL)
        {
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
     */
    private void fourtyFive() throws SymbolTableError, SemanticError
    {
        EType etype = (EType) stack.pop();
        if (etype == EType.RELATIONAL)
        {
            List E2False = (List) stack.pop();
            List E2True = (List) stack.pop();
            Token operator = (Token) stack.pop();

            if (getOpCode(operator).equals("and"))
            {
                List E1False = (List) stack.pop();
                stack.pop();

                List EFalse = merge(E1False, E2False);
                stack.push(E2True);
                stack.push(EFalse);
                stack.push(EType.RELATIONAL);
            }
        } else
        {
            // Pushed in #46
            SymbolTableEntry id2 = (SymbolTableEntry) stack.pop();
            // Pushed in #44
            Token operator = (Token) stack.pop();
            // Pushed in #46
            String opcode = getOpCode(operator);
            SymbolTableEntry id1 = (SymbolTableEntry) stack.pop();

            if (typeCheck(id1, id2) != 0 && (opcode.equals("div") || opcode.equals("MOD")))
            {
                // MOD and DIV require integer operands
                throw SemErr.badParameter("Operands of the " + opcode.toLowerCase() +
                        " operator must both be integers", operator);
            }

            // Are id1 and id2 both integers?
            if (typeCheck(id1, id2) == 0)
            {
                // Handle MOD and DIV keywords
                if (opcode.equals("MOD"))
                {
                    VariableEntry temp1 = createTemp(TokenType.INTEGER);
                    VariableEntry temp2 = createTemp(TokenType.INTEGER);
                    VariableEntry temp3 = createTemp(TokenType.INTEGER);
                    generate("div", id1, id2, temp1);
                    generate("mul", id2, temp1, temp2);
                    generate("sub", id1, temp2, temp3);
                    stack.push(temp3);
                } else if (opcode.equals("DIV"))
                {
                    VariableEntry temp1 = createTemp(TokenType.REAL);
                    VariableEntry temp2 = createTemp(TokenType.REAL);
                    VariableEntry temp3 = createTemp(TokenType.REAL);
                    generate("ltof", id1, temp1);
                    generate("ltof", id2, temp2);
                    generate("fdiv", temp1, temp2, temp3);
                    stack.push(temp3);
                } else
                {
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
     */
    private void checkAdd(SymbolTableEntry id1, SymbolTableEntry id2, String opcode) throws SymbolTableError
    {
        if (opcode.equals("DIV")) opcode = "div";
        // id1 and id2 are both reals
        int typeCheck = typeCheck(id1, id2);
        if (typeCheck == 1)
        {
            VariableEntry temp = createTemp(TokenType.REAL);
            generate("f" + opcode, id1, id2, temp);
            stack.push(temp);
        } else if (typeCheck == 2)
        { // id1 and id2 are different types of numbers
            VariableEntry temp1 = createTemp(TokenType.REAL);
            VariableEntry temp2 = createTemp(TokenType.REAL);
            generate("ltof", id2, temp1);
            generate("f" + opcode, id1, temp1, temp2);
            stack.push(temp2);
        } else
        {
            VariableEntry temp1 = createTemp(TokenType.REAL);
            VariableEntry temp2 = createTemp(TokenType.REAL);
            generate("ltof", id1, temp1);
            generate("f" + opcode, temp1, id2, temp2);
            stack.push(temp2);
        }
    }


    /**
     * Push identifiers & constants onto the stack for evaluation in an expression
     *
     * @param token identifier or constant
     */
    private void fourtySix(Token token) throws SemanticError, SymbolTableError
    {
        if (token.getType() == TokenType.IDENTIFIER)
        {
            // look for the token in the global or local symbol table
            SymbolTableEntry id = lookupId(token);
            // if token is not found
            if (id == null)
                throw SemErr.undeclaredVariable(token);

            stack.push(id);
        } else if (token.getType() == TokenType.INTCONSTANT || token.getType() == TokenType.REALCONSTANT)
        {
            // look for the token in the constant symbol table
            SymbolTableEntry id = constantTable.search(token.getValue().toString());
            // if not found add it to the constant table
            if (id == null)
            {
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
    private void fourtySeven(Token token) throws SemanticError
    {
        EType etype = (EType) stack.pop();
        if (etype != EType.RELATIONAL)
            throw SemErr.eTypeError(etype, token);

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
    private void fourtyEight(Token token) throws SymbolTableError, SemanticError
    {
        SymbolTableEntry offset = (SymbolTableEntry) stack.pop();
        if (offset != null)
        {
            if (offset.isFunction())
            {
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
     * Ensure this is a function & get parameter data.
     */
    private void fourtyNine(Token token) throws SemanticError
    {
        // get etype and id but do not change the stack
        EType etype = (EType) stack.pop();
        // id should be a function
        SymbolTableEntry id = (SymbolTableEntry) stack.peek();
        stack.push(etype);

        if (etype != EType.ARITHMETIC)
        {
            throw SemErr.eTypeError(etype, token);
        }
        if (!id.isFunction())
        {
            throw SemErr.illegalProcedure(id);
        }
        paramCount.push(0);
        paramStack.push(((FPEntry) id).getParamInfo());
    }

    /**
     * Generate code to assign memory for function parameters & call function.
     */
    private void fifty(Token token) throws SemanticError, SymbolTableError
    {
        // the parameters must be generated from the bottom-most to
        // the top-most
        Stack<SymbolTableEntry> parameters = createParamStack();

        // generate code for each of the parameters
        while (!parameters.empty())
        {
            SymbolTableEntry param = parameters.pop();
            generate("param", getParamPrefix(param) + getSTEAddress(param));
            localMemory++;
        }

        stack.pop(); //Etype

        // Ensure correct number of parameters
        FunctionEntry id = (FunctionEntry) stack.pop();
        int numParams = paramCount.pop();
        if (numParams > id.getParams())
        {
            throw SemErr.badNumberParams(id, id.getParams(), numParams, token);
        }

        // If so, generate a call to the function
        generate("call", id.getName(), Integer.toString(numParams));
        paramStack.pop();
        nextParam = 0;

        VariableEntry temp = createTemp(id.getResult().getType());
        generate("move", id.getResult(), temp);
        stack.push(temp);
        stack.push(EType.ARITHMETIC);
    }

    /**
     * Generate code to assign memory for procedure parameters & call procedure.
     */
    private void fiftyOne(Token token) throws SemanticError, SymbolTableError
    {
        // get all of the parameters on the stack
        Stack<SymbolTableEntry> parameters = createParamStack();

        EType etype = (EType) stack.pop();
        ProcedureEntry id = (ProcedureEntry) stack.pop();
        String name = id.getName();

        if (name.equals("READ") || name.equals("WRITE"))
        {
            // replace everything on the stack and call 51WRITE
            stack.push(id);
            stack.push(etype);
            while (!parameters.empty())
            {
                stack.push(parameters.pop());
            }
            if (name.equals("READ"))
            {
                execute(57, null, token);
            } else
            { // id is WRITE
                execute(58, null, token);
            }
        } else
        {
            // Ensure correct number of parameters
            int numParams = paramCount.pop();
            if (numParams != id.getParams())
            {
                throw SemErr.badNumberParams(id, id.getParams(), numParams, token);
            }

            // Generate code for each parameter
            while (!parameters.empty())
            {
                SymbolTableEntry param = parameters.pop();
                generate("param", getParamPrefix(param) + getSTEAddress(param));
                localMemory++;
            }
            // Call procedure
            generate("call", id.getName(), Integer.toString(numParams));
            paramStack.pop();
            nextParam = 0;
        }

    }

    /**
     * Helper function for actions 50 and 51.
     * Gets parameters from stack in correct order
     */
    private Stack<SymbolTableEntry> createParamStack()
    {
        Stack<SymbolTableEntry> parameters = new Stack<>();

        // for each parameter on the stack
        SymbolTableEntry top = (SymbolTableEntry) stack.peek();
        while (top.isArray() || top.isConstant() || top.isVariable())
        {
            parameters.push((SymbolTableEntry) stack.pop());
            if (!(stack.peek() instanceof SymbolTableEntry))
                break;
            top = (SymbolTableEntry) stack.peek();
        }
        return parameters;
    }

    /**
     * Read input from user.
     */
    private void fiftyOneR() throws SymbolTableError
    {
        // for every parameter on the stack in reverse order
        Stack<SymbolTableEntry> parameters = new Stack<>();
        SymbolTableEntry top = (SymbolTableEntry) stack.peek();
        while (top.isVariable())
        {
            parameters.push(((SymbolTableEntry) stack.pop()));
            if (!(stack.peek() instanceof SymbolTableEntry))
                break;
            top = (SymbolTableEntry) stack.peek();
        }

        while (!parameters.empty())
        {
            SymbolTableEntry id = parameters.pop();
            if (id.getType() == TokenType.REAL)
            {
                generate("finp", getSTEPrefix(id) + getSTEAddress(id));
            } else
            {
                generate("inp", getSTEPrefix(id) + getSTEAddress(id));
            }
        }
        stack.pop(); // EType
        stack.pop(); // Ste
        paramCount.pop();

    }

    /**
     * Display variable name and contents.
     */
    private void fiftyOneW() throws SymbolTableError
    {
        // for each parameter on the stack in reverse order
        Stack<SymbolTableEntry> parameters = new Stack<>();
        SymbolTableEntry top = (SymbolTableEntry) stack.peek();
        while (top.isConstant() || top.isVariable())
        {
            parameters.push((SymbolTableEntry) stack.pop());
            if (!(stack.peek() instanceof SymbolTableEntry))
                break;
            top = (SymbolTableEntry) stack.peek();
        }

        while (!parameters.empty())
        {
            SymbolTableEntry id = parameters.pop();
            if (id.isConstant())
            {
                if (id.getType() == TokenType.REAL)
                {
                    generate("foutp", id.getName());
                } else
                { // id.getType() == INTEGER
                    generate("outp", id.getName());
                }
            } else
            { // id is a variable entry
                if (id.getName().equals("$$temp106"))
                    System.out.println("hi");
                generate("print", "\"" + id.getName() + " = \"");
                if (id.getType() == TokenType.REAL)
                {
                    generate("foutp", getSTEPrefix(id) + getSTEAddress(id));
                } else
                { // id.getType() == INTEGER
                    generate("outp", getSTEPrefix(id) + getSTEAddress(id));
                }
            }
            generate("newl");
        }
        stack.pop(); // Etype
        stack.pop(); // Ste
        paramCount.pop();
    }

    /**
     * Case for function with no parameters.
     */
    private void fiftyTwo(Token token) throws SemanticError, SymbolTableError
    {
        stack.pop();
        SymbolTableEntry id = (SymbolTableEntry) stack.pop();
        if (!id.isFunction())
        {
            throw SemErr.illegalProcedure(id);
        }
        FunctionEntry idF = (FunctionEntry) id;
        if (idF.getParams() > 0)
        {
            throw SemErr.badNumberParams(idF, 0, idF.getParams(), token);
        }
        generate("call", id.getName(), "0");
        VariableEntry temp = createTemp(id.getType());
        generate("move", idF.getResult(), temp);
        stack.push(temp);
        stack.push(null);
    }


    /**
     * Lookup variable or function result
     */
    private void fiftyThree() throws SemanticError
    {
        EType etype = (EType) stack.pop();
        SymbolTableEntry id = (SymbolTableEntry) stack.pop();
        if (id.isFunction())
        {
            FunctionEntry fnId = (FunctionEntry) id;
            if (fnId != currentFunction)
                throw SemErr.illegalProcedure(id);

            stack.push(fnId.getResult());
            stack.push(EType.ARITHMETIC);

        } else
        {
            stack.push(id);
            stack.push(etype);
        }
    }

    /**
     * Confirm statement is a procedure call
     */
    private void fiftyFour() throws SemanticError
    {
        EType etype = (EType) stack.pop();
        SymbolTableEntry id = (SymbolTableEntry) stack.peek();
        stack.push(etype);
        if (!id.isProcedure())
        {
            throw SemErr.illegalProcedure(id);
        }
    }

    /**
     * Backpatches global memory to ensure the correct amount is allocated at the start.
     * Frees that much global memory at the end.
     */
    private void fiftyFive()
    {
        backpatch(globalStore, globalMemory);
        generate("free", Integer.toString(globalMemory));
        generate("PROCEND");
    }

    /**
     * Adds the first couple instructions
     */
    private void fiftySix()
    {
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
    private int getSTEAddress(SymbolTableEntry ste) throws SymbolTableError
    {
        int address = 0;
        if (ste.isArray() || ste.isVariable())
        {
            // array entries and variable entries are
            // assigned address when they are initialized
            address = Math.abs(ste.getAddress());
        } else if (ste.isConstant())
        {
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
    private String getSTEPrefix(SymbolTableEntry ste)
    {
        if (global)
            return "_";
        else
        { // local
            // If it's a constant we're about to generate the temp in getSTEAddr
            if (ste.isConstant())
                return "%";
            SymbolTableEntry entry = localTable.search(ste.getName());
            if (entry == null)  // entry is a global variable
                return "_";
            else
            {
                if (ste.isParameter())
                    return "^%";
                else
                    return "%";
            }
        }
    }


    // Generate Methods, each of these generate a new quadruple (string array)
    // Many overloaded methods for the various parameters that generate could be called on

    /**
     * Universal generate method. Called by other generate methods.
     */
    private void generate(String... operands)
    {
        String[] quadEntry = new String[operands.length];
        // First entry is code
        quadEntry[0] = operands[0];

        // Copy array passed in to rest of quad entries
        System.arraycopy(operands, 1, quadEntry, 1, operands.length - 1);

        quads.add(quadEntry);
    }

    private void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, SymbolTableEntry
            operand3) throws SymbolTableError
    {
        generate(tviCode, steAddr(operand1), steAddr(operand2), steAddr(operand3));
    }

    private void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, String operand3) throws SymbolTableError
    {
        generate(tviCode, steAddr(operand1), steAddr(operand2), operand3);
    }

    private void generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2) throws
            SymbolTableError
    {
        generate(tviCode, steAddr(operand1), steAddr(operand2));
    }

    private void generate(String tviCode, String operand1, SymbolTableEntry operand2) throws SymbolTableError
    {
        generate(tviCode, operand1, steAddr(operand2));
    }


    /**
     * For a symbol table entry, returns a string for the local or global address
     * of that entry. Used by generate methods.
     */
    private String steAddr(SymbolTableEntry ste) throws SymbolTableError
    {
        return getSTEPrefix(ste) + getSTEAddress(ste);
    }

    private VariableEntry create(String name, TokenType type) throws SymbolTableError
    {
        VariableEntry ve = new VariableEntry(name, type);
        // Global or local?
        if (global)
        {
            ve.setAddress(-1 * globalMemory);
            globalMemory++;
            globalTable.insert(ve);
        } else
        {
            ve.setAddress(localMemory);
            localMemory++;
            localTable.insert(ve);
        }
        return ve;
    }

    /**
     * Creates a new variable entry and inserts it into the proper symbol table.
     *
     * @param type type of variable
     * @return the variable entry
     * @throws SymbolTableError if a variable with this name is already in the symbol table
     */
    private VariableEntry createTemp(TokenType type) throws SymbolTableError
    {
        tempCt++;
        return create("$$temp" + tempCt, type);
    }

    /**
     * Checks type of 2 integers/reals
     *
     * @param id1 integer or real
     * @param id2 integer or real
     * @return 0-3 based on relationship
     */
    private int typeCheck(SymbolTableEntry id1, SymbolTableEntry id2)
    {
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
    private SymbolTableEntry lookupId(Token token)
    {
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
    private void backpatch(int i, int x)
    {
        quads.get(i)[1] = Integer.toString(x);
    }

    private void backpatch(List list, int x)
    {
        for (Object val : list)
        {
            // Eliminates "Unchecked type warning"
            int i = (int) val;
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
    private String getOpCode(Token opToken)
    {
        switch (opToken.getType())
        {
            case ADDOP:
                switch ((int) opToken.getValue())
                {
                    case 1:
                        return "add";
                    case 2:
                        return "sub";
                    case 3:
                        return "or";
                }
                break;
            case MULOP:
                switch ((int) opToken.getValue())
                {
                    case 1:
                        return "mul";
                    // division operator (/)
                    case 2:
                        return "DIV";
                    // DIV keyword
                    case 3:
                        return "div";
                    case 4:
                        return "MOD";
                    case 5:
                        return "and";
                }
                break;
            case UNARYMINUS:
                return "uminus";
            case RELOP:
                switch ((int) opToken.getValue())
                {
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

    private List merge(List l1, List l2)
    {
        // Going to some length to avoid an unchecked type warning, ensuring type safety
        return Stream.of(l1.toArray(), l2.toArray()).flatMap(Stream::of).collect(Collectors.toList());
    }

    private String getParamPrefix(SymbolTableEntry param)
    {
        if (global)
            return "@_";
        else
        { // local
            if (param.isParameter())
                return "%";
            else
                return "@%";
        }
    }

    /**
     * Gets the intermediate code from quads in a pretty format
     *
     * @return String representation of generated intermediate code
     */
    public String getInterCode()
    {
        StringBuilder out = new StringBuilder("CODE\n");

        for (int i = 1; i < quads.size(); i++)
        {
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
    public SymbolTable getGlobalTable()
    {
        return globalTable;
    }

    public SymbolTable getConstantTable()
    {
        return constantTable;
    }

    public SymbolTable getLocalTable()
    {
        return localTable;
    }

    public Stack<Object> getStack()
    {
        return stack;
    }
}
