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
    private boolean array = false;
    private int nextParam;
    private int localStore = 0;
    private int globalStore = 0;
    private int globalMemory = 0;
    private int localMemory = 0;
    private int tempCt = 0;
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
                globalTable.Insert(entry);
            }

            for (String res : new String[]{"INPUT", "OUTPUT"})
            {
                entry = new IODeviceEntry(res);
                entry.setReserved(true);
                globalTable.Insert(entry);
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
    public void Execute(int num, Token token, Token prevToken) throws SemanticError, SymbolTableError
    {
        switch (num)
        {
            case 3:
                Three();
                break;
            case 4:
                stack.push(prevToken);
                break;
            case 5:
                Five();
                break;
            case 6:
                array = true;
                break;
            case 7:
                stack.push(prevToken);
                break;
            case 9:
                Nine();
                break;
            case 11:
                Eleven();
                break;
            case 13:
                stack.push(prevToken);
                break;
            case 15:
                Fifteen(prevToken);
                break;
            case 16:
                Sixteen();
                break;
            case 17:
                Seventeen(prevToken);
                break;
            case 19:
                Nineteen();
                break;
            case 20:
                Twenty();
                break;
            case 21:
                TwentyOne();
                break;
            case 22:
                TwentyTwo(token);
                break;
            case 24:
                //Store line number of beginning of loop.
                stack.push(quads.size());
                break;
            case 25:
                TwentyFive(token);
                break;
            case 26:
                TwentySix();
                break;
            case 27:
                TwentySeven();
                break;
            case 28:
                TwentyEight();
                break;
            case 29:
                TwentyNine();
                break;
            case 30:
                Thirty(prevToken);
                break;
            case 31:
                ThirtyOne(prevToken);
                break;
            case 32:
                ThirtyTwo(token);
                break;
            case 33:
                ThirtyThree(prevToken);
                break;
            case 34:
                ThirtyFour(prevToken);
                break;
            case 35:
                ThirtyFive();
                break;
            case 36:
                ThirtySix(prevToken);
                break;
            case 37:
                ThirtySeven(prevToken);
                break;
            case 38:
                ThirtyEight(prevToken);
                break;
            case 39:
                ThirtyNine(prevToken);
                break;
            case 40:
                stack.push(prevToken);
                break;
            case 41:
                FourtyOne(token);
                break;
            case 42:
                FourtyTwo(prevToken);
                break;
            case 43:
                FourtyThree();
                break;
            case 44:
                FourtyFour(prevToken);
                break;
            case 45:
                FourtyFive();
                break;
            case 46:
                FourtySix(prevToken);
                break;
            case 47:
                FourtySeven(token);
                break;
            case 48:
                FourtyEight(prevToken);
                break;
            case 49:
                FourtyNine(prevToken);
                break;
            case 50:
                Fifty(prevToken);
                break;
            case 51:
                FiftyOne(prevToken);
                break;
            case 52:
                FiftyTwo(prevToken);
                break;
            case 53:
                FiftyThree();
                break;
            case 54:
                FiftyFour();
                break;
            case 55:
                FiftyFive();
                break;
            case 56:
                FiftySix();
                break;
            case 57:
                FiftyOneR();
                break;
            case 58:
                FiftyOneW();
                break;
        }
    }

    // Semantic Actions

    /**
     * Semantic action 3, handles declarations of arrays and variables
     *
     * @throws SymbolTableError if an id is added to the local or global symbol table that already exists
     */
    private void Three() throws SymbolTableError
    {
        TokenType type = ((Token) stack.pop()).GetType();
        AVEntry entry;
        int memorySize = 1;
        int upperBound = 0;
        int lowerBound = 0;
        // If we have an array figure out memory based on bounds
        // Otherwise its a variable so memory is 1
        if (array)
        {
            upperBound = Integer.parseInt(((Token) stack.pop()).GetValue().toString());
            lowerBound = Integer.parseInt(((Token) stack.pop()).GetValue().toString());
            memorySize = (upperBound - lowerBound) + 1;
        }

        while (!stack.isEmpty() && (stack.peek() instanceof Token && (((Token) stack.peek()).GetType() == TokenType.IDENTIFIER)))
        {
            String name = ((Token) stack.pop()).GetValue().toString();
            // Create array or variable entry
            entry = array
                    ? new ArrayEntry(name, type, upperBound, lowerBound)
                    : new VariableEntry(name, type);

            // Add to local or global symbol table
            if (global)
            {
                entry.SetAddress(-1 * globalMemory);
                globalTable.Insert(entry);
                globalMemory += memorySize;
            }
            else
            {
                entry.SetAddress(localMemory);
                localTable.Insert(entry);
                localMemory += memorySize;
            }
        }
        array = false;
    }

    /**
     * Generate code for start of function.
     */
    private void Five()
    {
        SymbolTableEntry id = (SymbolTableEntry) stack.pop();
        Generate("PROCBEGIN", id.getName());
        localStore = quads.size();
        // Will be Backpatched
        Generate("alloc", "_");
    }

    /**
     * Adds the name of the program to the global table.
     */
    private void Nine() throws SymbolTableError
    {
        stack.pop();
        stack.pop();
        Token id3 = (Token) stack.pop();

        SymbolTableEntry entry = new ProcedureEntry(id3.toString(), 0, new ArrayList<>());
        entry.setReserved(true);
        globalTable.Insert(entry);

        Generate("call", "main", "0");
        Generate("exit");
    }

    /**
     * Generate code for end of function.
     */
    private void Eleven()
    {
        global = true;
        // delete the local symbol table
        localTable = new SymbolTable();
        currentFunction = null;
        // Backpatch allocation for local memory
        Backpatch(localStore, localMemory);
        Generate("free", Integer.toString(localMemory));
        Generate("PROCEND");
    }

    /**
     * Store result of function
     */
    private void Fifteen(Token token) throws SymbolTableError
    {
        // create a variable to store the result of the function
        VariableEntry result = Create(token.GetValue() + "_RESULT", TokenType.INTEGER);
        // create a new function entry with name from the token
        // from the parser and the result variable just created
        FPEntry id = new FunctionEntry(token.GetValue().toString(), result);
        globalTable.Insert(id);
        global = false;
        localMemory = 0;
        currentFunction = id;
        stack.push(id);
    }

    /**
     * Sets the type of the function and its result.
     */
    private void Sixteen()
    {
        Token type = (Token) stack.pop();
        FunctionEntry id = (FunctionEntry) stack.peek();
        id.setType(type.GetType());
        // set the type of the result variable of id
        id.SetResultType(type.GetType());
        currentFunction = id;
    }

    /**
     * Create procedure in symbol table.
     */
    private void Seventeen(Token token) throws SymbolTableError
    {
        // create a new procedure entry with the name of the token
        // from the parser
        FPEntry id = new ProcedureEntry(token.GetValue().toString());
        globalTable.Insert(id);
        global = false;
        localMemory = 0;
        currentFunction = id;
        stack.push(id);
    }

    /**
     * Initialise count of formal parameters.
     */
    private void Nineteen()
    {
        paramCount = new Stack<>();
        paramCount.push(0);
    }

    /**
     * Get number of parameters.
     */
    private void Twenty()
    {
        FPEntry id = (FPEntry) stack.peek();
        int numParams = paramCount.pop();
        // id is a function entry or a procedure entry
        id.SetParams(numParams);
    }

    /**
     * Create temporary variables to store parameter info.
     */
    private void TwentyOne() throws SymbolTableError
    {
        Token type = (Token) stack.pop();

        // if array, then pop the upper and lower bounds
        int upperBound = -1;
        int lowerBound = -1;
        if (array)
        {
            upperBound = Integer.parseInt(((Token) stack.pop()).GetValue().toString());
            lowerBound = Integer.parseInt(((Token) stack.pop()).GetValue().toString());
        }

        // the tokens on the stack, which represent parameters,
        // must be added from the bottom-most id to the top-most
        Stack<Token> parameters = new Stack<>();

        // as the ids are popped off the stack, push them onto to
        // the new stack to reverse the order
        Object top = stack.peek();
        while (top instanceof Token && ((Token) top).GetType() == TokenType.IDENTIFIER)
        {
            parameters.push((Token) stack.pop());
            top = stack.peek();
        }

        while (!parameters.empty())
        {
            Token param = parameters.pop();
            AVEntry var;
            if (array)
                var = new ArrayEntry(param.GetValue().toString(), localMemory,
                        type.GetType(), upperBound, lowerBound);
            else
                var = new VariableEntry(param.GetValue().toString(), localMemory, type.GetType());

            var.SetParameter();
            localTable.Insert(var);
            // current function is either a procedure or function entry
            currentFunction.AddParameter(var);
            localMemory++;
            // increment the top of paramCount
            paramCount.push(paramCount.pop() + 1);
        }
        array = false;

    }

    /**
     * Update branch destination for IF -> #t to next quad.
     */
    private void TwentyTwo(Token token) throws SemanticError
    {
        EType etype = (EType) stack.pop();
        if (etype != EType.RELATIONAL)
            throw SemErr.eTypeError(etype, token);

        // Always casting to generic List instead of List<Integer> to avoid unchecked cast warnings
        List EFalse = (List) stack.pop();
        List ETrue = (List) stack.pop();
        // Backpatch destinations in ETRUE using the number of the next quad (where the TRUE case is).
        Backpatch(ETrue, quads.size());
        stack.push(ETrue);
        stack.push(EFalse);
    }

    /**
     * Initialisation for a WHILE loop.
     * This code is the same as action 22, but it happens in the case of a while loop instead of an if.
     */
    private void TwentyFive(Token token) throws SemanticError
    {
        TwentyTwo(token);
    }

    /**
     * Write code at end of WHILE loop.
     */
    private void TwentySix()
    {
        List EFalse = (List) stack.pop();
        stack.pop();
        // beginLoop is pushed onto the stack in action 24. It's the start of the loop
        Generate("goto", Integer.toString((int) stack.pop()));
        // Go to the next line when the condition fails
        Backpatch(EFalse, quads.size());
    }

    /**
     * Sets up else case
     */
    private void TwentySeven()
    {
        // Where to go after else is the next Generated code
        // skipElse is the first line of the else code
        List skipElse = Collections.singletonList(quads.size());
        Generate("goto", "_"); // Here it is!
        List EFalse = (List) stack.pop();
        List ETrue = (List) stack.pop();
        // Backpatch EFalse with the line number of skipElse
        Backpatch(EFalse, quads.size());
        stack.push(skipElse);
        // Put ETrue and EFalse back on the stack
        stack.push(ETrue);
        stack.push(EFalse);
    }

    /**
     * End of else statement.
     */
    private void TwentyEight()
    {
        stack.pop();
        stack.pop();
        // skipElse is pushed onto the stack in action 27. Skip else is where we go after the else.
        List skipElse = (List) stack.pop();
        // Backpatch where we go after else case
        Backpatch(skipElse, quads.size());
    }

    /**
     * End of if without else.
     */
    private void TwentyNine()
    {
        List EFalse = (List) stack.pop();
        stack.pop();
        // There is no else case so the false case is just the next line
        Backpatch(EFalse, quads.size());
    }

    /**
     * Check to see if a variable has been declared
     *
     * @param token identifier variable
     * @throws SemanticError if the variable has not been declared
     */
    private void Thirty(Token token) throws SemanticError
    {
        SymbolTableEntry id = LookupId(token);
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
    private void ThirtyOne(Token token) throws SymbolTableError, SemanticError
    {
        SymbolTableEntry id2 = CheckEType(token);
        // offset will be implemented in later actions
        SymbolTableEntry offset = (SymbolTableEntry) stack.pop();
        SymbolTableEntry id1 = (SymbolTableEntry) stack.pop();

        // We'll put the value of id2 in id1
        if (TypeCheck(id1, id2) == 3)
            throw SemErr.typeMismatch("Integer", "Real", token);

        if (TypeCheck(id1, id2) == 2)
        {
            VariableEntry temp = CreateTemp(TokenType.REAL);
            Generate("ltof", id2, temp);
            if (offset == null)
                Generate("move", temp, id1);
            else
                Generate("stor", temp, offset, id1);
        }
        else
        {
            if (offset == null)
                Generate("move", id2, id1);
            else
                Generate("stor", id2, offset, id1);
        }
    }

    /**
     * Ensures top of stack is an array
     */
    private void ThirtyTwo(Token token) throws SemanticError
    {
        EType etype = (EType) stack.pop();
        SymbolTableEntry id = (SymbolTableEntry) stack.peek();
        if (etype != EType.ARITHMETIC)
            throw SemErr.eTypeError(etype, token);

        if (!id.IsArray())
            throw SemErr.idIsNotArray(id, token);

    }

    /**
     * Calculate memory offset for array element.
     */
    private void ThirtyThree(Token token) throws SemanticError, SymbolTableError
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
        VariableEntry temp1 = CreateTemp(TokenType.INTEGER);
        // Memory offset
        VariableEntry temp2 = CreateTemp(TokenType.INTEGER);
        Generate("move", Integer.toString(array.GetLowBound()), temp1);
        Generate("sub", id, temp1, temp2);
        stack.push(temp2);
    }

    /**
     * Function or procedure.
     */
    private void ThirtyFour(Token token) throws SemanticError, SymbolTableError
    {
        EType etype = (EType) stack.pop();
        SymbolTableEntry id = (SymbolTableEntry) stack.peek();
        if (id.IsFunction())
        {
            stack.push(etype);
            Execute(52, null, token);
        }
        else
            stack.push(null);
    }

    /**
     * Set up to call a procedure.
     */
    private void ThirtyFive()
    {
        EType etype = (EType) stack.pop();
        // id is a procedure entry
        FPEntry id = (ProcedureEntry) stack.peek();
        stack.push(etype);
        paramCount.push(0);
        paramStack.push(id.GetParamInfo());
    }

    /**
     * Generate code to call a procedure.
     */
    private void ThirtySix(Token token) throws SemanticError
    {
        stack.pop();
        ProcedureEntry id = (ProcedureEntry) stack.pop();
        if (id.GetParams() != 0)
        {
            throw SemErr.badNumberParams(id, 0, id.GetParams(), token);
        }
        Generate("call", id.getName(), "0");
    }

    /**
     * Consume actual parameters in a list of parameters.
     */
    private void ThirtySeven(Token token) throws SemanticError
    {
        EType etype = (EType) stack.pop();
        if (etype != EType.ARITHMETIC)
        {
            throw SemErr.eTypeError(etype, token);
        }

        SymbolTableEntry id = (SymbolTableEntry) stack.peek();
        if (id.IsProcedure() || id.IsFunction())
        {
            throw SemErr.badParameterType(currentFunction, id, token);
        }

        // increment the top of paramCount
        paramCount.push(paramCount.pop() + 1);

        // find the name of the procedure/function on the bottom of the stack
        Stack<Object> parameters = new Stack<>();
        // Add parameters to temp stack until we hit a function or procedure
        while (!(stack.peek() instanceof SymbolTableEntry) || !(((SymbolTableEntry) stack.peek()).IsFunction() || ((SymbolTableEntry) stack.peek()).IsProcedure()))
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
        }
        else
            funcId = (FPEntry) stack.peek();

        // Add parameters to stack (now in correct order)
        while (!parameters.empty())
        {
            stack.push(parameters.pop());
        }

        String name = funcId.getName();
        // Consume parameters from stack and ensure they match number and type
        // required by the function or procedure.
        if (!(name.equals("READ") || name.equals("WRITE")))
        {
            if (paramCount.peek() > funcId.GetParams())
            {
                throw SemErr.badNumberParams(funcId, funcId.GetParams(), paramCount.peek(), token);
            }
            SymbolTableEntry param = paramStack.peek().get(nextParam);
            TokenType retType = param.getType();
            if (id.getType() != retType && !((retType == TokenType.INTEGER && id.getType() == TokenType.INTCONSTANT) || (retType == TokenType.REAL && id.getType() == TokenType.REALCONSTANT)))
            {
                throw SemErr.badParameterType(funcId, id, param, token);
            }
            if (param.IsArray())
            {
                ArrayEntry paramArr = (ArrayEntry) param;
                ArrayEntry idArr = (ArrayEntry) id;
                if ((idArr.GetLowBound() != paramArr.GetLowBound()) || (idArr.GetUpBound() != paramArr.GetUpBound()))
                {
                    throw SemErr.badParameterType(funcId, id, param, token);
                }
            }
            nextParam++;
        }

    }

    /**
     * Ensure arithmetic operation and push
     */
    private void ThirtyEight(Token token) throws SemanticError
    {
        EType etype = (EType) stack.pop();
        if (etype != EType.ARITHMETIC)
            throw SemErr.eTypeError(etype, token);
        // token should be an operator
        stack.push(token);
    }

    /**
     * Change to relational and add ETrue/EFalse as required
     */
    private void ThirtyNine(Token token) throws SemanticError, SymbolTableError
    {
        EType etype = (EType) stack.pop();
        // Ensure it is a arithop
        if (etype != EType.ARITHMETIC)
            throw SemErr.eTypeError(etype, token);

        SymbolTableEntry id2 = (SymbolTableEntry) stack.pop();
        Token operator = (Token) stack.pop();
        // the operator must be replaced with the proper TVI code which
        // jump if the condition is meant
        String opcode = GetOpCode(operator);
        SymbolTableEntry id1 = (SymbolTableEntry) stack.pop();
        // Generate appropriate code based on types of ids
        if (TypeCheck(id1, id2) == 2)
        {
            VariableEntry temp = CreateTemp(TokenType.REAL);
            Generate("ltof", id2, temp);
            Generate(opcode, id1, temp, "_");
        }
        else if (TypeCheck(id1, id2) == 3)
        {
            VariableEntry temp = CreateTemp(TokenType.REAL);
            Generate("ltof", id1, temp);
            Generate(opcode, temp, id2, "_");
        }
        else
            Generate(opcode, id1, id2, "_");

        Generate("goto", "_");
        // Addresses to Backpatch in #22
        List ETrue = Collections.singletonList(quads.size() - 2);
        List EFalse = Collections.singletonList(quads.size() - 1);
        stack.push(ETrue);
        stack.push(EFalse);
        stack.push(EType.RELATIONAL);
    }

    /**
     * Apply unary plus/minus
     */
    private void FourtyOne(Token token) throws SymbolTableError, SemanticError
    {
        SymbolTableEntry id = CheckEType(token);
        Token sign = (Token) stack.pop();

        // If the operator is uminus, create a temp var to store the result
        if (sign.GetType() == TokenType.UNARYMINUS)
        {
            TokenType type = id.getType();
            VariableEntry temp = CreateTemp(type);
            // Integer or float?
            if (id.getType() == TokenType.INTEGER || id.getType() == TokenType.INTCONSTANT)
                Generate("uminus", id, temp);
            else
                Generate("fuminus", id, temp);

            stack.push(temp);
        }
        else
            stack.push(id);

        stack.push(EType.ARITHMETIC);
    }

    /**
     * Helper function for actions 31 and 41 to avoid duplicate code
     *
     * @return Symbol table entry from stack if EType.ARITHMETIC was on stack before
     * @throws SemanticError if top of stack is not EType.ARITHMETIC
     */
    private SymbolTableEntry CheckEType(Token token) throws SemanticError
    {
        EType eType = (EType) stack.pop();

        if (eType != EType.ARITHMETIC)
            throw SemErr.eTypeError(eType, token);

        return (SymbolTableEntry) stack.pop();
    }

    /**
     * Backpatches 'or' blocks.
     */
    private void FourtyTwo(Token token) throws SemanticError
    {
        EType etype = (EType) stack.pop();

        if (GetOpCode(token).equals("or"))
        {
            if (etype != EType.RELATIONAL)
                throw SemErr.eTypeError(etype, token);
            // the top of the stack should be a list of integers
            List EFalse = (List) stack.peek();
            Backpatch(EFalse, quads.size());
        }
        else
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
    private void FourtyThree() throws SymbolTableError
    {
        EType etype = (EType) stack.pop();
        if (etype == EType.RELATIONAL)
        {
            List E2False = (List) stack.pop();
            List E2True = (List) stack.pop();
            stack.pop();
            stack.pop();
            List E1True = (List) stack.pop();

            List ETrue = Merge(E1True, E2True);
            stack.push(ETrue);
            stack.push(E2False);
            stack.push(EType.RELATIONAL);
        }
        else
        { // if etype == EType.ARITHMETIC
            SymbolTableEntry id2 = (SymbolTableEntry) stack.pop();
            // this is one place where the operator from action 42 is popped
            Token operator = (Token) stack.pop();
            // get the TVI opcode associated with the operator token
            // ex. for a token representing addition, opcode would be "add"
            String opcode = GetOpCode(operator);
            SymbolTableEntry id1 = (SymbolTableEntry) stack.pop();

            // Both integers?
            if (TypeCheck(id1, id2) == 0)
            {
                VariableEntry temp = CreateTemp(TokenType.INTEGER);
                Generate(opcode, id1, id2, temp);
                stack.push(temp);
            }
            else
                CheckAdd(id1, id2, opcode);
            stack.push(EType.ARITHMETIC);
        }
    }

    /**
     * Backpatches 'and' blocks
     */
    private void FourtyFour(Token token)
    {
        if (stack.pop() == EType.RELATIONAL)
        {
            List EFalse = (List) stack.pop();
            List ETrue = (List) stack.pop();
            // and is represented by a series of 'beq_,_,_,  goto' statements, the beq goes to where we want if each
            // boolean in the 'and' is true, the goto jump somewhere if any boolean in the 'and' is false
            // Here, we want to Backpatch ETrue to 2 addresses forward because we know there'll be a goto directly following
            if (GetOpCode(token).equals("and"))
                Backpatch(ETrue, quads.size());

            stack.push(ETrue);
            stack.push(EFalse);
        }

        stack.push(token);
    }

    /**
     * Evaluate multiplication, division, modular arithmetic, and AND.
     */
    private void FourtyFive() throws SymbolTableError, SemanticError
    {
        EType etype = (EType) stack.pop();
        if (etype == EType.RELATIONAL)
        {
            List E2False = (List) stack.pop();
            List E2True = (List) stack.pop();
            Token operator = (Token) stack.pop();

            if (GetOpCode(operator).equals("and"))
            {
                List E1False = (List) stack.pop();
                stack.pop();

                List EFalse = Merge(E1False, E2False);
                stack.push(E2True);
                stack.push(EFalse);
                stack.push(EType.RELATIONAL);
            }
        }
        else
        {
            // Pushed in #46
            SymbolTableEntry id2 = (SymbolTableEntry) stack.pop();
            // Pushed in #44
            Token operator = (Token) stack.pop();
            // Pushed in #46
            String opcode = GetOpCode(operator);
            SymbolTableEntry id1 = (SymbolTableEntry) stack.pop();

            if (TypeCheck(id1, id2) != 0 && (opcode.equals("div") || opcode.equals("MOD")))
            {
                // MOD and DIV require integer operands
                throw SemErr.badParameterType("Operands of the " + opcode.toLowerCase() +
                        " operator must both be integers", operator);
            }

            // Are id1 and id2 both integers?
            if (TypeCheck(id1, id2) == 0)
            {
                // Handle MOD and DIV keywords
                if (opcode.equals("MOD"))
                {
                    VariableEntry temp1 = CreateTemp(TokenType.INTEGER);
                    VariableEntry temp2 = CreateTemp(TokenType.INTEGER);
                    VariableEntry temp3 = CreateTemp(TokenType.INTEGER);
                    Generate("div", id1, id2, temp1);
                    Generate("mul", id2, temp1, temp2);
                    Generate("sub", id1, temp2, temp3);
                    stack.push(temp3);
                }
                else if (opcode.equals("DIV"))
                {
                    VariableEntry temp1 = CreateTemp(TokenType.REAL);
                    VariableEntry temp2 = CreateTemp(TokenType.REAL);
                    VariableEntry temp3 = CreateTemp(TokenType.REAL);
                    Generate("ltof", id1, temp1);
                    Generate("ltof", id2, temp2);
                    Generate("fdiv", temp1, temp2, temp3);
                    stack.push(temp3);
                }
                else
                {
                    // Generate for 2 integers
                    VariableEntry temp = CreateTemp(TokenType.INTEGER);
                    Generate(opcode, id1, id2, temp);
                    stack.push(temp);
                }
            }
            else
                CheckAdd(id1, id2, opcode);
            stack.push(EType.ARITHMETIC);
        }
    }


    /**
     * Helper function for actions 45 and 46, their final else case is the same code.
     * Performs an operation when either id1 or id2 is not an integer.
     */
    private void CheckAdd(SymbolTableEntry id1, SymbolTableEntry id2, String opcode) throws SymbolTableError
    {
        if (opcode.equals("DIV")) opcode = "div";
        // id1 and id2 are both reals
        int typeCheck = TypeCheck(id1, id2);
        if (typeCheck == 1)
        {
            VariableEntry temp = CreateTemp(TokenType.REAL);
            Generate("f" + opcode, id1, id2, temp);
            stack.push(temp);
        }
        else if (typeCheck == 2)
        { // id1 and id2 are different types of numbers
            VariableEntry temp1 = CreateTemp(TokenType.REAL);
            VariableEntry temp2 = CreateTemp(TokenType.REAL);
            Generate("ltof", id2, temp1);
            Generate("f" + opcode, id1, temp1, temp2);
            stack.push(temp2);
        }
        else
        {
            VariableEntry temp1 = CreateTemp(TokenType.REAL);
            VariableEntry temp2 = CreateTemp(TokenType.REAL);
            Generate("ltof", id1, temp1);
            Generate("f" + opcode, temp1, id2, temp2);
            stack.push(temp2);
        }
    }


    /**
     * Look up value of variable or constant from the SymbolTable
     *
     * @param token identifier or constant
     */
    private void FourtySix(Token token) throws SemanticError, SymbolTableError
    {
        if (token.GetType() == TokenType.IDENTIFIER)
        {
            // look for the token in the global or local symbol table
            SymbolTableEntry id = LookupId(token);
            // if token is not found
            if (id == null)
                throw SemErr.undeclaredVariable(token);

            stack.push(id);
        }
        else if (token.GetType() == TokenType.INTCONSTANT || token.GetType() == TokenType.REALCONSTANT)
        {
            // look for the token in the constant symbol table
            SymbolTableEntry id = constantTable.Search(token.GetValue().toString());
            // if not found add it to the constant table
            if (id == null)
            {
                id = new ConstantEntry(token.GetValue().toString(), token.GetType());
                constantTable.Insert(id);
            }
            stack.push(id);
        }
        stack.push(EType.ARITHMETIC);
    }

    /**
     * Handles NOT reserved word.
     */
    private void FourtySeven(Token token) throws SemanticError
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
     * Array lookup.
     */
    private void FourtyEight(Token token) throws SymbolTableError, SemanticError
    {
        SymbolTableEntry offset = (SymbolTableEntry) stack.pop();
        if (offset != null)
        {
            if (offset.IsFunction())
            {
                // call action 52 with the token from the parser
                Execute(52, null, token);
            }
            SymbolTableEntry id = (SymbolTableEntry) stack.pop();
            VariableEntry temp = CreateTemp(id.getType());
            Generate("load", id, offset, temp);
            stack.push(temp);
        }
        stack.push(EType.ARITHMETIC);
    }

    /**
     * Ensure this is a function & get parameter data.
     */
    private void FourtyNine(Token token) throws SemanticError
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
        if (!id.IsFunction())
        {
            throw SemErr.illegalProcedure(id);
        }
        paramCount.push(0);
        paramStack.push(((FPEntry) id).GetParamInfo());
    }

    /**
     * Generate code to assign memory for function parameters & call function.
     */
    private void Fifty(Token token) throws SemanticError, SymbolTableError
    {
        // the parameters must be Generated from the bottom-most to
        // the top-most
        Stack<SymbolTableEntry> parameters = CreateParamStack();

        // Generate code for each of the parameters
        while (!parameters.empty())
        {
            SymbolTableEntry param = parameters.pop();
            Generate("param", GetParamPrefix(param) + GetSTEAddress(param));
            localMemory++;
        }

        stack.pop(); //Etype

        // Ensure correct number of parameters
        FunctionEntry id = (FunctionEntry) stack.pop();
        int numParams = paramCount.pop();
        if (numParams > id.GetParams())
        {
            throw SemErr.badNumberParams(id, id.GetParams(), numParams, token);
        }

        // If so, Generate a call to the function
        Generate("call", id.getName(), Integer.toString(numParams));
        paramStack.pop();
        nextParam = 0;

        VariableEntry temp = CreateTemp(id.GetResult().getType());
        Generate("move", id.GetResult(), temp);
        stack.push(temp);
        stack.push(EType.ARITHMETIC);
    }

    /**
     * Generate code to assign memory for procedure parameters & call procedure.
     */
    private void FiftyOne(Token token) throws SemanticError, SymbolTableError
    {
        // get all of the parameters on the stack
        Stack<SymbolTableEntry> parameters = CreateParamStack();

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
                Execute(57, null, token);
            }
            else
            { // id is WRITE
                Execute(58, null, token);
            }
        }
        else
        {
            // Ensure correct number of parameters
            int numParams = paramCount.pop();
            if (numParams != id.GetParams())
            {
                throw SemErr.badNumberParams(id, id.GetParams(), numParams, token);
            }

            // Generate code for each parameter
            while (!parameters.empty())
            {
                SymbolTableEntry param = parameters.pop();
                Generate("param", GetParamPrefix(param) + GetSTEAddress(param));
                localMemory++;
            }
            // Call procedure
            Generate("call", id.getName(), Integer.toString(numParams));
            paramStack.pop();
            nextParam = 0;
        }

    }

    /**
     * Helper function for actions 50 and 51.
     * Gets parameters from stack in correct order.
     */
    private Stack<SymbolTableEntry> CreateParamStack()
    {
        Stack<SymbolTableEntry> parameters = new Stack<>();

        // for each parameter on the stack
        SymbolTableEntry top = (SymbolTableEntry) stack.peek();
        while (top.IsArray() || top.isConstant() || top.isVariable())
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
    private void FiftyOneR() throws SymbolTableError
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
                Generate("finp", GetSTEPrefix(id) + GetSTEAddress(id));
            }
            else
            {
                Generate("inp", GetSTEPrefix(id) + GetSTEAddress(id));
            }
        }
        stack.pop(); // EType
        stack.pop(); // Ste
        paramCount.pop();

    }

    /**
     * Display variable name and contents.
     */
    private void FiftyOneW() throws SymbolTableError
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
                    Generate("foutp", id.getName());
                }
                else
                { // id.getType() == INTEGER
                    Generate("outp", id.getName());
                }
            }
            else
            { // id is a variable entry
                Generate("print", "\"" + id.getName() + " = \"");
                if (id.getType() == TokenType.REAL)
                {
                    Generate("foutp", GetSTEPrefix(id) + GetSTEAddress(id));
                }
                else
                { // id.getType() == INTEGER
                    Generate("outp", GetSTEPrefix(id) + GetSTEAddress(id));
                }
            }
            Generate("newl");
        }
        stack.pop(); // Etype
        stack.pop(); // Ste
        paramCount.pop();
    }

    /**
     * Case for function with no parameters.
     */
    private void FiftyTwo(Token token) throws SemanticError, SymbolTableError
    {
        stack.pop();
        SymbolTableEntry id = (SymbolTableEntry) stack.pop();
        if (!id.IsFunction())
        {
            throw SemErr.illegalProcedure(id);
        }
        FunctionEntry idF = (FunctionEntry) id;
        if (idF.GetParams() > 0)
        {
            throw SemErr.badNumberParams(idF, 0, idF.GetParams(), token);
        }
        Generate("call", id.getName(), "0");
        VariableEntry temp = CreateTemp(id.getType());
        Generate("move", idF.GetResult(), temp);
        stack.push(temp);
        stack.push(null);
    }


    /**
     * Lookup variable or function result.
     */
    private void FiftyThree() throws SemanticError
    {
        EType etype = (EType) stack.pop();
        SymbolTableEntry id = (SymbolTableEntry) stack.pop();
        if (id.IsFunction())
        {
            FunctionEntry fnId = (FunctionEntry) id;
            if (fnId != currentFunction)
                throw SemErr.illegalProcedure(id);

            stack.push(fnId.GetResult());
            stack.push(EType.ARITHMETIC);

        }
        else
        {
            stack.push(id);
            stack.push(etype);
        }
    }

    /**
     * Confirm statement is a procedure call.
     */
    private void FiftyFour() throws SemanticError
    {
        EType etype = (EType) stack.pop();
        SymbolTableEntry id = (SymbolTableEntry) stack.peek();
        stack.push(etype);
        if (!id.IsProcedure())
        {
            throw SemErr.illegalProcedure(id);
        }
    }

    /**
     * Backpatches global memory to ensure the correct amount is allocated at the start.
     * Frees that much global memory at the end.
     */
    private void FiftyFive()
    {
        Backpatch(globalStore, globalMemory);
        Generate("free", Integer.toString(globalMemory));
        Generate("PROCEND");
    }

    /**
     * Generate start of main, adds the first couple instructions.
     */
    private void FiftySix()
    {
        Generate("PROCBEGIN", "main");
        globalStore = quads.size();
        // the underscore as the second arguement in Generate
        // is a placeholder that will be filled in later by Backpatch
        Generate("alloc", "_");
    }

    // Helper Methods

    /**
     * Gets the address of a symbol table entry
     *
     * @param ste array, variable, or constant entry
     * @return address of entry
     */
    private int GetSTEAddress(SymbolTableEntry ste) throws SymbolTableError
    {
        int address = 0;
        if (ste.IsArray() || ste.isVariable())
        {
            // array entries and variable entries are
            // assigned address when they are initialized
            address = Math.abs(ste.GetAddress());
        }
        else if (ste.isConstant())
        {
            // constants do not have an address, and a
            // temporary variable must be created to store it
            ConstantEntry entry = (ConstantEntry) ste;
            VariableEntry temp = CreateTemp(entry.getType());
            // move the constant into the temporary variable
            Generate("move", ste.getName(), temp);
            // return the address of the temporary variable
            address = Math.abs(temp.GetAddress());
        }
        return address;
    }

    /**
     * Gets the prefix of a symbol table entry
     *
     * @param ste entry to get prefix for
     * @return '_' for global '%' for local
     */
    private String GetSTEPrefix(SymbolTableEntry ste)
    {
        if (global)
            return "_";
        else
        { // local
            // If it's a constant we're about to Generate the temp in getSTEAddr
            if (ste.isConstant())
                return "%";
            SymbolTableEntry entry = localTable.Search(ste.getName());
            if (entry == null)  // entry is a global variable
                return "_";
            else
            {
                if (ste.IsParameter())
                    return "^%";
                else
                    return "%";
            }
        }
    }


    // Generate Methods, each of these Generate a new quadruple (string array)
    // Many overloaded methods for the various parameters that Generate could be called on

    /**
     * Universal Generate method. Called by other Generate methods.
     *
     * @param operands any number of string operands to be put in a quadruple
     */
    private void Generate(String... operands)
    {
        String[] quadEntry = new String[operands.length];
        // First entry is code
        quadEntry[0] = operands[0];

        // Copy array passed in to rest of quad entries
        System.arraycopy(operands, 1, quadEntry, 1, operands.length - 1);

        quads.add(quadEntry);
    }

    /**
     * Generate method with all SymbolTableEntry parameters.
     */
    private void Generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, SymbolTableEntry
            operand3) throws SymbolTableError
    {
        // Get the TVI address representation of each entry and call universal generate.
        Generate(tviCode, SteAddr(operand1), SteAddr(operand2), SteAddr(operand3));
    }

    /**
     * Generate method.
     */
    private void Generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2, String operand3) throws SymbolTableError
    {
        Generate(tviCode, SteAddr(operand1), SteAddr(operand2), operand3);
    }

    /**
     * Generate method.
     */
    private void Generate(String tviCode, SymbolTableEntry operand1, SymbolTableEntry operand2) throws
            SymbolTableError
    {
        Generate(tviCode, SteAddr(operand1), SteAddr(operand2));
    }

    /**
     * Generate method.
     */
    private void Generate(String tviCode, String operand1, SymbolTableEntry operand2) throws SymbolTableError
    {
        Generate(tviCode, operand1, SteAddr(operand2));
    }


    /**
     * For a symbol table entry, returns a string for the local or global address
     * of that entry. Used by Generate methods.
     */
    private String SteAddr(SymbolTableEntry ste) throws SymbolTableError
    {
        return GetSTEPrefix(ste) + GetSTEAddress(ste);
    }

    /**
     * Creates a new Variable. Mostly used to create temporary variables.
     *
     * @param name name of the variable
     * @param type type of the variable
     * @return VariableEntry object for this variable
     * @throws SymbolTableError if variable with the requested name was already declared in the scope
     */
    private VariableEntry Create(String name, TokenType type) throws SymbolTableError
    {
        VariableEntry ve = new VariableEntry(name, type);
        // Global or local?
        if (global)
        {
            ve.SetAddress(-1 * globalMemory);
            globalMemory++;
            globalTable.Insert(ve);
        }
        else
        {
            ve.SetAddress(localMemory);
            localMemory++;
            localTable.Insert(ve);
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
    private VariableEntry CreateTemp(TokenType type) throws SymbolTableError
    {
        tempCt++;
        return Create("$$temp" + tempCt, type);
    }

    /**
     * Checks type of 2 integers/reals
     *
     * @param id1 integer or real
     * @param id2 integer or real
     * @return 0-3 based on relationship
     */
    private int TypeCheck(SymbolTableEntry id1, SymbolTableEntry id2)
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
     * @param token with type Identifier
     * @return the symbol table entry if it exists or null otherwise
     */
    private SymbolTableEntry LookupId(Token token)
    {
        String id = token.GetValue().toString();
        // first look in the local table
        SymbolTableEntry ste = localTable.Search(id);
        // if id is not in the local table
        if (ste == null)
            // then look in the global table
            ste = globalTable.Search(id);

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
    private void Backpatch(int i, int x)
    {
        quads.get(i)[1] = Integer.toString(x);
    }

    private void Backpatch(List list, int x)
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
    private String GetOpCode(Token opToken)
    {
        switch (opToken.GetType())
        {
            case ADDOP:
                switch ((int) opToken.GetValue())
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
                switch ((int) opToken.GetValue())
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
                switch ((int) opToken.GetValue())
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

    /**
     * Merges two Lists
     */
    private List Merge(List l1, List l2)
    {
        // Going to some length to avoid an unchecked type warning, ensuring type safety
        return Stream.of(l1.toArray(), l2.toArray()).flatMap(Stream::of).collect(Collectors.toList());
    }

    /**
     * Gets the TVI prefix for a parameter's address.
     *
     * @param param parameter.
     * @return "@_" if global, "%" if local, "@%" if local but not tagged as a parameter.
     */
    private String GetParamPrefix(SymbolTableEntry param)
    {
        if (global)
            return "@_";
        else
        { // local
            if (param.IsParameter())
                return "%";
            else
                return "@%";
        }
    }

    /**
     * Gets the intermediate code from quads in a pretty format
     *
     * @return String representation of Generated intermediate code
     */
    public String GetInterCode()
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

    /**
     * Getter for global table
     */
    public SymbolTable GetGlobalTable()
    {
        return globalTable;
    }

    /**
     * Getter for constant table
     */
    public SymbolTable GetConstantTable()
    {
        return constantTable;
    }

    /**
     * Getter for local table
     */
    public SymbolTable GetLocalTable()
    {
        return localTable;
    }

    /**
     * Getter for stack
     */
    public Stack<Object> GetStack()
    {
        return stack;
    }
}
