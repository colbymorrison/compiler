package compiler.Lexer;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Stack;

import compiler.Exception.LexerError;

/**
 * This class implements a Lexer for the Vascal language
 */
public class Lexer {
    private final Scan scan;
    // HashTable mapping keywords to their Types, this should be in the symbol table
    // but we haven't written that yet
    private static final String VALID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890" +
            ".,;:<>/*[]+-=()}{$\t\r\n ";
    private final Hashtable<String, TokenType> table = new Hashtable<>();
    private final Stack<Character> pushBack = new Stack<>();
    private Token prevToken;

    /**
     * Constructor
     *
     * @param pathName The pathname of the file to read
     */
    public Lexer(String pathName) {
        // Create a new scan object from the pathname
        scan = new Scan(pathName);
        initTable();
    }

    /**
     * This method is called to get the next Token from the input. This implements
     * a DFA by calling a read...() method according to the character read. Each
     * read...() method implements a DFA to parse lexemes beginning with their
     * respective character.
     *
     * @return The parsed token
     */
    public Token getNextToken() throws LexerError {
        Token token;
        int row;
        int col;
        char c = getNextChar();

        if (!VALID_CHARS.contains(Character.toString(c)))
            throw LexerError.invalidCharacter(c, scan.getRow(), scan.getCol());

        // Skip whitespace
        while (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '{') {
            if (c == '{')
                // c will be set to next char after comment
                c = readComment();
            else
                c = getNextChar();
        }

        row = scan.getRow();
        col = scan.getCol();
        // Check for eof
        if (c == '$')
            token = new Token<>(TokenType.ENDOFFILE);
            // Otherwise, call correct method
        else if (Character.isLetter(c))
            token = readIdentifier(c);
        else if (Character.isDigit(c))
            token = readDigit(c);
        else if (c == '<')
            token = readLeftAngle();
        else if (c == '>')
            token = readRightAngle();
        else if (c == '+' || c == '-')
            token = readPlusMinus(c);
        else if (c == '.')
            token = readDot();
        else if (c == ':')
            token = readColon();
        else
            token = readSymbol(c);
        token.setCol(col);
        token.setRow(row);
        prevToken = token;
        return token;
    }

    /**
     * Reads through a comment and ensures it is valid.
     */
    private char readComment() throws LexerError {
        char ch;
        do {
            ch = getNextChar();
            if (ch == '}') {
                // Lookahead one character because we can't have }} in a comment
                ch = getNextChar();
                if (ch == '}')
                    throw LexerError.invalidComment(scan.getRow(), scan.getCol());
                else
                    break;
            }
        } while (ch != '$');
        return ch;
    }

    /**
     * Gets the next char to feed into the DFA.
     */
    private char getNextChar() throws LexerError {
        char ch;
        // If the stack is empty, get the next character from the reader.
        // Otherwise, we push back buy popping a character off the stack.
        if (pushBack.isEmpty()) {
            try {
                ch = scan.getNextChar();
            } catch (IOException ioe) {
                throw LexerError.ioError(ioe.getMessage());
            }
        } else {
            ch = pushBack.pop();
            scan.setMinCol(-1);
        }

        return ch;
    }

    /**
     * Implements a DFA to read identifiers and keywords
     */
    private Token readIdentifier(char ch) throws LexerError {
        StringBuilder buffer = new StringBuilder();

        // If we have a digit or letter, add it to the string
        while (Character.isDigit(ch) || Character.isLetter(ch)) {
            buffer.append(ch);
            ch = getNextChar();
        }

        // If the next character is not whitespace, it's non-aphanumeric, and
        // so we push it back
        if (!(ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r')) {
            pushBack.push(ch);
            scan.setMinCol(1);
        }

        // Cap keywords at length 32
        if (buffer.length() >= 32)
            throw LexerError.idTooLong(buffer.toString(), scan.getRow(), scan.getCol());

        String str = buffer.toString();
        // Check in table to see if we have a keyword, otherwise its an identifier
        if (table.containsKey(str)) {
            TokenType type = table.get(str);
            switch (str) {
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
    private Token readDigit(char ch) throws LexerError {
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

        while (true) {
            ch = getNextChar();
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
        if (accept_state[state]) {
            // Push back last character
            pushBack.push(ch);
            scan.setMinCol(1);
            // If we only saw digits, we have an int
            if (state == 0)
                return new Token<>(TokenType.INTCONSTANT, buffer.toString());
                // Here we have a real
            else if (state == 2 || state == 4 || state == 5)
                return new Token<>(TokenType.REALCONSTANT, buffer.toString());
                // Must be state 6, double dot.
            else {
                pushBack.push('.');
                pushBack.push('.');
                scan.setMinCol(2);
                // Buffer looks like 'INT..x' where x is some char, so our int is the start of the buffer
                // Until 4 from the end
                return new Token<>(TokenType.INTCONSTANT, buffer.substring(0, buffer.length() - 3));
            }
        } else
            throw LexerError.invalidConstant(scan.getRow(), scan.getCol());
    }

    /**
     * Reads tokens beginning with '<'
     */
    private Token readLeftAngle() throws LexerError {
        // Lookahead one char to see if '<>' or '<='
        char ch = getNextChar();
        if (ch == '>')
            return new Token<>(TokenType.RELOP, 2);
        else if (ch == '=')
            return new Token<>(TokenType.RELOP, 5);
        else {
            pushBack.push(ch);
            return new Token<>(TokenType.RELOP, 3);
        }
    }

    /**
     * Reads tokens beginning with '<'
     */
    private Token readRightAngle() throws LexerError {
        // Lookahead one char to see if '<='
        char ch = getNextChar();
        if (ch == '=')
            return new Token<>(TokenType.RELOP, 6);
        else {
            pushBack.push(ch);
            return new Token<>(TokenType.RELOP, 4);
        }
    }

    /**
     * Reads tokens beginning with '+' or '-'
     */
    private Token readPlusMinus(char ch) {
        TokenType type = prevToken.getType();

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
    private Token readColon() throws LexerError {
        // Lookahead one char to see if ':='
        char ch = getNextChar();
        if (ch == '=')
            return new Token<>(TokenType.ASSIGNOP);
        else {
            pushBack.push(ch);
            return new Token<>(TokenType.COLON);
        }
    }

    /**
     * Reads tokens beginning with '.'
     */
    private Token readDot() throws LexerError {
        // Lookahead one char to see if '..'
        char ch = getNextChar();
        if (ch == '.')
            return new Token<>(TokenType.DOUBLEDOT);
        else {
            pushBack.push(ch);
            return new Token<>(TokenType.ENDMARKER);
        }
    }

    /**
     * Reads token where type and value is unambiguous on reading character
     * i.e. no lookahead/pushback
     */
    private Token readSymbol(char ch) throws LexerError {
        switch (ch) {
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
                throw LexerError.invalidCharacter(ch, scan.getRow(), scan.getCol());
        }
    }

    /**
     * Initializes HashTable mapping keywords to their token type
     */
    private void initTable() {
        table.put("PROGRAM", TokenType.PROGRAM);
        table.put("BEGIN", TokenType.BEGIN);
        table.put("END", TokenType.END);
        table.put("VAR", TokenType.VAR);
        table.put("FUNCTION", TokenType.FUNCTION);
        table.put("PROCEDURE", TokenType.PROCEDURE);
        table.put("RESULT", TokenType.RESULT);
        table.put("INTEGER", TokenType.INTEGER);
        table.put("ARRAY", TokenType.ARRAY);
        table.put("OF", TokenType.OF);
        table.put("NOT", TokenType.NOT);
        table.put("IF", TokenType.IF);
        table.put("THEN", TokenType.THEN);
        table.put("ELSE", TokenType.ELSE);
        table.put("WHILE", TokenType.WHILE);
        table.put("DO", TokenType.DO);
        table.put("REAL", TokenType.REAL);
        table.put("DIV", TokenType.MULOP);
        table.put("MOD", TokenType.MULOP);
        table.put("AND", TokenType.MULOP);
        table.put("OR", TokenType.ADDOP);
    }
}


