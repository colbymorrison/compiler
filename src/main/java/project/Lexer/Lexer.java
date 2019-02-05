package project.Lexer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Stack;

import project.Exception.LexicalException;

public class Lexer {
    private Scan scan;
    private static final char[] whitespace = new char[]{' ', '\t', '\n', '\r'};
    // This should be done in the symbol table, but we haven't made that yet
    private final Hashtable<String, TokenType> table = new Hashtable<>();
    private Stack<Character> pushBack = new Stack<>();
    private Token prevToken;

    public Lexer(String s) throws LexicalException, IOException {
        scan = new Scan(s);
        initTable();
    }

    /**
     * This method is called to get the next Token from the input. This implements
     * a DFA by calling a read...() method according to the character read. Each
     * read...() method implements a DFA to parse lexemes beginning with their
     * respective character.
     *
     * @return The parsed token
     * @throws LexicalException if an error occured
     */
    public Token getNextToken() throws LexicalException {
        Token token;
        char c = getNextChar();
//        System.out.println("Character: "+c);
        // Skip whitespace
        while (c == ' ' || c == '\t' || c == '\n')
            c = getNextChar();
        if ((int) c == 3)
            token = new Token<>(TokenType.ENDOFFILE);
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
        else if (c == '.') {
            token = readDot();
        } else if (c == ':') {
            token = readColon();
        } else
            token = readSymbol(c);
        prevToken = token;
        System.out.println(token);
        return token;
    }

    private char getNextChar() throws LexicalException {
        char ch;
        if (pushBack.isEmpty()) {
            try {
                ch = scan.getNextChar();
            } catch (IOException ioe) {
                throw LexicalException.ioError(ioe.getMessage());
            }
        } else
            ch = pushBack.pop();
        return ch;
    }

    private Token readIdentifier(char ch) throws LexicalException {
        StringBuilder buffer = new StringBuilder();

        while (Character.isDigit(ch) || Character.isLetter(ch)) {
            buffer.append(ch);
            // Set a mark at stream
            try {
                scan.getReader().mark(1);
            } catch (IOException ioe) {
                throw LexicalException.ioError(ioe.getMessage());
            }
            ch = getNextChar();
        }

        if (!(ch == ' ' || ch == '\t' || ch == '\n')){
            // Reset stream
            try {
                scan.getReader().reset();
            } catch (IOException ioe) {
                throw LexicalException.ioError(ioe.getMessage());
            }
        }

        if(buffer.length() >= 32)
            throw LexicalException.idTooLong(scan.getRow(), scan.getCol());

        String str = buffer.toString();
        if (table.containsKey(str)) {
            TokenType type = table.get(str);
            switch (str) {
                case "OR":
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

    private Token readDigit(char ch) throws LexicalException {
        // We know we've got one digit
        StringBuilder buffer = new StringBuilder();
        buffer.append(ch);

        // Here, the DFA is a bit more complex so we'll define the transition table
        // instead of using loops
        // Index in each state array: digit = 0, dot = 1, E = 2, +/- = 3, else 4
        boolean[] accept_state = new boolean[]{true, false, true, false, true, true, true};
        int err = 7;
        int state = 0;
        int[][] trans = new int[][]{
                new int[]{0, 1, 3, err, err}, // State 0
                new int[]{2, 6, err, err, err}, // State 1
                new int[]{2, err, 3, err, err}, // State 2
                new int[]{5, err, err, 4, err}, // State 3
                new int[]{5, err, err, err, err},// State 4
                new int[]{5, err, err, err, err},// State 5
                new int[]{err, err, err, err, err},// State 6
                new int[]{} // Error state
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
                // Done with lexeme
            else
                break;
            // Add character to buffer
            buffer.append(ch);

            // Are we going to error state or not?
            int next = trans[state][idx];
            if (next == err)
                break;
            state = next;
        }

        if (accept_state[state]) {
            // Push back last character
            pushBack.push(ch);
            if (state == 0)
                return new Token<>(TokenType.INTCONSTANT, buffer.toString());
            else if (state == 2 || state == 4|| state == 5)
                return new Token<>(TokenType.REALCONSTANT, buffer.toString());
                // We have double dot, push back extra dot
            else {
                pushBack.push('.');
                pushBack.push('.');
                return new Token<>(TokenType.INTCONSTANT, Integer.parseInt(Character.toString(buffer.charAt(0))));
            }
        } else
            throw LexicalException.illFormedConstant(scan.getRow(), scan.getCol());
    }

    private Token readLeftAngle() throws LexicalException {
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

    private Token readRightAngle() throws LexicalException {
        char ch = getNextChar();
        if (ch == '=')
            return new Token<>(TokenType.RELOP, 6);
        else {
            pushBack.push(ch);
            return new Token<>(TokenType.RELOP, 4);
        }
    }

    private Token readPlusMinus(char ch) {
        TokenType type = prevToken.getType();

        if (type == TokenType.RPAREN || type == TokenType.RBRACKET || type == TokenType.IDENTIFIER ||
                type == TokenType.INTCONSTANT || type == TokenType.REALCONSTANT)
            return new Token<>(TokenType.ADDOP, ch == '+' ? 1 : 2);
        else
            return new Token<>(ch == '+' ? TokenType.UNARYPLUS : TokenType.UNARYMINUS); //TODO could also be a constant

    }

    private Token readColon() throws LexicalException {
        char ch = getNextChar();
        if (ch == '=')
            return new Token<>(TokenType.ASSIGNOP);
        else {
            pushBack.push(ch);
            return new Token<>(TokenType.COLON);
        }
    }

    private Token readDot() throws LexicalException {
        char ch = getNextChar();
        if (ch == '.')
            return new Token<>(TokenType.DOUBLEDOT);
        else
            return new Token<>(TokenType.ENDMARKER);
    }

    // ALl symbols where there is only one type option for the symbol
    private Token readSymbol(char ch) throws LexicalException {
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
                throw LexicalException.invalidCharacter(ch, scan.getRow(), scan.getCol());
        }
    }

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


