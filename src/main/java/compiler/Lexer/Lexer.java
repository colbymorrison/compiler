package compiler.Lexer;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Stack;

import compiler.Exception.GenLexErr;
import compiler.Exception.LexerError;

/**
 * This class implements a Lexer for the Vascal language
 */
public class Lexer
{
    private final Scan Scan;
    // HashTable mapping keywords to their Types, this should be in the symbol table
    // but we haven't written that yet
    private static final String VALID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890" +
            ".,;:<>/*[]+-=()}{$\t\r\n ";
    private final Hashtable<String, TokenType> Table = new Hashtable<>();
    private final Stack<Character> PushBack = new Stack<>();
    private Token PrevToken;
    private GenLexErr LexErr = new GenLexErr();

    /**
     * Constructor
     *
     * @param pathName The pathname of the file to read
     */
    public Lexer(String pathName)
    {
        // Create a new scan object from the pathname
        Scan = new Scan(pathName);
        InitTable();
    }

    /**
     * This method is called to get the next Token from the input. This implements
     * a DFA by calling a read...() method according to the character read. Each
     * read...() method implements a DFA to parse lexemes beginning with their
     * respective character.
     *
     * @return The parsed token
     */
    public Token GetNextToken() throws LexerError
    {
        Token token;
        int row;
        int col;
        char c = GetNextChar();

        if (!VALID_CHARS.contains(Character.toString(c)))
            throw LexErr.InvalidCharacter(c, Scan.GetRow(), Scan.GetCol());

        // Skip whitespace
        while (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '{')
        {
            if (c == '{')
                // c will be set to next char after comment
                c = ReadComment();
            else
                c = GetNextChar();
        }

        row = Scan.GetRow();
        col = Scan.GetCol();
        // Check for eof
        if (c == '$')
            token = new Token<>(TokenType.ENDOFFILE);
            // Otherwise, call correct method
        else if (Character.isLetter(c))
            token = ReadIdentifier(c);
        else if (Character.isDigit(c))
            token = ReadDigit(c);
        else if (c == '<')
            token = ReadLeftAngle();
        else if (c == '>')
            token = ReadRightAngle();
        else if (c == '+' || c == '-')
            token = ReadPlusMinus(c);
        else if (c == '.')
            token = ReadDot();
        else if (c == ':')
            token = ReadColon();
        else
            token = readSymbol(c);
        token.SetCol(col);
        token.SetRow(row);
        PrevToken = token;
        return token;
    }

    /**
     * Reads through a comment and ensures it is valid.
     */
    private char ReadComment() throws LexerError
    {
        char ch;
        do
        {
            ch = GetNextChar();
            if (ch == '}')
            {
                // Lookahead one character because we can't have }} in a comment
                ch = GetNextChar();
                if (ch == '}')
                    throw LexErr.InvalidComment(Scan.GetRow(), Scan.GetCol());
                else
                    break;
            }
        } while (ch != '$');
        return ch;
    }

    /**
     * Gets the next char to feed into the DFA.
     */
    private char GetNextChar() throws LexerError
    {
        char ch;
        // If the stack is empty, get the next character from the reader.
        // Otherwise, we push back buy popping a character off the stack.
        if (PushBack.isEmpty())
        {
            try
            {
                ch = Scan.GetNextChar();
            } catch (IOException ioe)
            {
                throw LexErr.IoError(ioe.getMessage());
            }
        } else
        {
            ch = PushBack.pop();
            Scan.SetMinCol(-1);
        }

        return ch;
    }

    /**
     * Implements a DFA to read identifiers and keywords
     */
    private Token ReadIdentifier(char ch) throws LexerError
    {
        StringBuilder buffer = new StringBuilder();

        // If we have a digit or letter, add it to the string
        while (Character.isDigit(ch) || Character.isLetter(ch))
        {
            buffer.append(ch);
            ch = GetNextChar();
        }

        // If the next character is not whitespace, it's non-aphanumeric, and
        // so we push it back
        if (!(ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r'))
        {
            PushBack.push(ch);
            Scan.SetMinCol(1);
        }

        // Cap keywords at length 32
        if (buffer.length() >= 32)
            throw LexErr.IdTooLong(buffer.toString(), Scan.GetRow(), Scan.GetCol());

        String str = buffer.toString();
        // Check in table to see if we have a keyword, otherwise its an identifier
        if (Table.containsKey(str))
        {
            TokenType type = Table.get(str);
            switch (str)
            {
                case "OR": // OR falls through to div
                case "DIV":
                    return new Token<>(type, 3);
                case "MOD":
                    return new Token<>(type, 4);
                case "AND":
                    return new Token<>(type, 5);
                default:
                    return new Token<String>(type);
            }
        } else
            return new Token<>(TokenType.IDENTIFIER, str);
    }

    /**
     * DFA to read a digit and decide if there's an int, float, or double dot.
     * Here, the DFA is a bit more complex so we'll define a transition table.
     */
    private Token ReadDigit(char ch) throws LexerError
    {
        // Buffer to build string
        StringBuilder buffer = new StringBuilder();
        buffer.append(ch);


        boolean[] accept_state = new boolean[]{true, false, true, false, true, true, true};
        int err = 7;
        int state = 0;
        // Transition table, each inner int[] represents a state. Each possible character
        // in the DFA is associated with an index into a state array that contains the next state
        // if that character is read while in that state.
        // The indices are: digit = 0, dot = 1, E = 2, +/- = 3, else 4
        int[][] trans = new int[][]{
                new int[]{0, 1, 3, err}, // State 0
                new int[]{2, 6, err, err}, // State 1
                new int[]{2, err, 3, err}, // State 2
                new int[]{5, err, err, 4}, // State 3
                new int[]{5, err, err, err},// State 4
                new int[]{5, err, err, err},// State 5
                new int[]{err, err, err, err},// State 6
        };

        while (true)
        {
            ch = GetNextChar();
            // Index into state table
            int idx;
            if (Character.isDigit(ch))
                idx = 0;
            else if (ch == '.')
                idx = 1;
            else if (ch == 'E')
                idx = 2;
            else if (ch == '+' || ch == '-')
                idx = 3;
                // Invalid character for int or real, break
            else
                break;
            // Add character to buffer
            buffer.append(ch);

            // Are we going to error state?
            int next = trans[state][idx];
            if (next == err)
                break;
            state = next;
        }

        // If we ended in an accept state, add int or real constant based on which state
        if (accept_state[state])
        {
            // Push back last character
            PushBack.push(ch);
            Scan.SetMinCol(1);
            // If we only saw digits, we have an int
            if (state == 0)
                return new Token<>(TokenType.INTCONSTANT, buffer.toString());
                // Here we have a real
            else if (state == 2 || state == 4 || state == 5)
                return new Token<>(TokenType.REALCONSTANT, buffer.toString());
                // Must be state 6, double dot.
            else
            {
                PushBack.push('.');
                PushBack.push('.');
                Scan.SetMinCol(2);
                // Buffer looks like 'INT..x' where x is some char, so our int is the start of the buffer
                // Until 4 from the end
                return new Token<>(TokenType.INTCONSTANT, buffer.substring(0, buffer.length() - 3));
            }
        } else
            throw LexErr.InvalidConstant(Scan.GetRow(), Scan.GetCol());
    }

    /**
     * Reads tokens beginning with '<'
     */
    private Token ReadLeftAngle() throws LexerError
    {
        // Lookahead one char to see if '<>' or '<='
        char ch = GetNextChar();
        if (ch == '>')
            return new Token<>(TokenType.RELOP, 2);
        else if (ch == '=')
            return new Token<>(TokenType.RELOP, 5);
        else
        {
            PushBack.push(ch);
            return new Token<>(TokenType.RELOP, 3);
        }
    }

    /**
     * Reads tokens beginning with '<'
     */
    private Token ReadRightAngle() throws LexerError
    {
        // Lookahead one char to see if '<='
        char ch = GetNextChar();
        if (ch == '=')
            return new Token<>(TokenType.RELOP, 6);
        else
        {
            PushBack.push(ch);
            return new Token<>(TokenType.RELOP, 4);
        }
    }

    /**
     * Reads tokens beginning with '+' or '-'
     */
    private Token ReadPlusMinus(char ch)
    {
        TokenType type = PrevToken.GetType();

        // Use previous token to determine if we have an addop or unaryop
        if (type == TokenType.RPAREN || type == TokenType.RBRACKET || type == TokenType.IDENTIFIER ||
                type == TokenType.INTCONSTANT || type == TokenType.REALCONSTANT)
            return new Token<>(TokenType.ADDOP, ch == '+' ? 1 : 2);
        else
            return new Token<>(ch == '+' ? TokenType.UNARYPLUS : TokenType.UNARYMINUS);

    }

    /**
     * Reads tokens beginning with ':'
     */
    private Token ReadColon() throws LexerError
    {
        // Lookahead one char to see if ':='
        char ch = GetNextChar();
        if (ch == '=')
            return new Token<>(TokenType.ASSIGNOP);
        else
        {
            PushBack.push(ch);
            return new Token<>(TokenType.COLON);
        }
    }

    /**
     * Reads tokens beginning with '.'
     */
    private Token ReadDot() throws LexerError
    {
        // Lookahead one char to see if '..'
        char ch = GetNextChar();
        if (ch == '.')
            return new Token<>(TokenType.DOUBLEDOT);
        else
        {
            PushBack.push(ch);
            return new Token<>(TokenType.ENDMARKER);
        }
    }

    /**
     * Reads token where type and value is unambiguous on reading character
     * i.e. no lookahead/pushback
     */
    private Token readSymbol(char ch) throws LexerError
    {
        switch (ch)
        {
            case '*':
                return new Token<>(TokenType.MULOP, 1);
            case '/':
                return new Token<>(TokenType.MULOP, 2);
            case ',':
                return new Token<>(TokenType.COMMA);
            case ';':
                return new Token<>(TokenType.SEMICOLON);
            case '(':
                return new Token<>(TokenType.LPAREN);
            case ')':
                return new Token<>(TokenType.RPAREN);
            case '[':
                return new Token<>(TokenType.LBRACKET);
            case ']':
                return new Token<>(TokenType.RBRACKET);
            case '=':
                return new Token<>(TokenType.RELOP, 1);
            default:
                throw LexErr.InvalidCharacter(ch, Scan.GetRow(), Scan.GetCol());
        }
    }

    /**
     * Initializes HashTable mapping keywords to their token type
     */
    private void InitTable()
    {
        Table.put("PROGRAM", TokenType.PROGRAM);
        Table.put("BEGIN", TokenType.BEGIN);
        Table.put("END", TokenType.END);
        Table.put("VAR", TokenType.VAR);
        Table.put("FUNCTION", TokenType.FUNCTION);
        Table.put("PROCEDURE", TokenType.PROCEDURE);
        Table.put("RESULT", TokenType.RESULT);
        Table.put("INTEGER", TokenType.INTEGER);
        Table.put("ARRAY", TokenType.ARRAY);
        Table.put("OF", TokenType.OF);
        Table.put("NOT", TokenType.NOT);
        Table.put("IF", TokenType.IF);
        Table.put("THEN", TokenType.THEN);
        Table.put("ELSE", TokenType.ELSE);
        Table.put("WHILE", TokenType.WHILE);
        Table.put("DO", TokenType.DO);
        Table.put("REAL", TokenType.REAL);
        Table.put("DIV", TokenType.MULOP);
        Table.put("MOD", TokenType.MULOP);
        Table.put("AND", TokenType.MULOP);
        Table.put("OR", TokenType.ADDOP);
    }
}


