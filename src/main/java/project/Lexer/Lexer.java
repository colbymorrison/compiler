package project.Lexer;

import java.io.IOException;
import java.util.Hashtable;
import project.Exception.LexicalException;

public class Lexer {
    private Scan scan;
    // This should be done in the symbol table, but we haven't made that yet
    private final Hashtable<String, TokenType> table = new Hashtable<>();
    private final String WHITESPACE = " \t\n";
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
        char c = scan.getNextChar();
        if(WHITESPACE.contains(Character.toString(c)))
            c = scan.getNextChar();
        if (Character.isLetter(c))
            token = readIdentifier(c);
        else if (Character.isDigit(c))
            token = readDigit(c);
        else if (c == '<')
            token = readLeftAngle(c);
        else if (c == '>')
            token = readRightAngle(c);
        else if (c == '+' || c == '-')
            token = readPlusMinus(c);
        else if (c == '.')
            token = readDot(c);
        // TODO handle
        else
            token = readSymbol(c);
        prevToken = token;
        return token;
    }

    private Token readIdentifier(char ch) throws LexicalException {
        StringBuilder buffer = new StringBuilder();

        while (Character.isDigit(ch) || Character.isLetter(ch)) {
            buffer.append(ch);
            // Set a mark at stream
            try {
                scan.getReader().mark(1);
            } catch (IOException ioe) {
                throw new LexicalException("IO error");
            }
            ch = scan.getNextChar();
        }

        if (!WHITESPACE.contains(Character.toString(ch))) {
            // Reset stream
            try {
                scan.getReader().reset();
            } catch (IOException ioe) {
                throw new LexicalException("IO error");
            }
        }

        String str = buffer.toString();
        if (table.containsKey(str)) {
            TokenType type = table.get(str);
            /// token = Token.createToken(type);
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
        // digit = 0, dot = 1, E = 2, +/- = 3, else 4
        boolean[] accept_state = new boolean[]{true, false, true, false, false, true};
        int error = 6;
        int state = 0;
        int[][] trans = new int[][]{
                new int[]{0, 1, 6, 6, 6}, // State 0
                new int[]{2, 6, 6, 6, 6}, // State 1
                new int[]{2, 6, 3, 6, 6}, // State 2
                new int[]{5, 6, 6, 4, 6},
                new int[]{5, 6, 6, 6, 6},
                new int[]{5, 6, 6, 6, 6},
                new int[]{} // Error state
        };

        while (true) {
            ch = scan.getNextChar();
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
            if (next == error)
                break;
            state = next;
        }

        // If we only saw digits, then we have an int
        if (accept_state[state]) {
            if (state == 0)
                return new Token<>(TokenType.INTCONSTANT, Integer.parseInt(buffer.toString()));
            else
                return new Token<>(TokenType.REALCONSTANT, buffer.toString());
        } else {
            throw new LexicalException("Invalid input");
        }
    }

    private Token readLeftAngle() throws LexicalException {
        scan.getReader().mark(1);
        char ch = scan.getNextChar();
        if(ch == '>')
            return new Token<>(TokenType.RELOP, 2);
        else if(ch == '=')
            return new Token<>(TokenType.RELOP, 5);
        else{
           scan.getReader().reset();
           return new Token<>(TokenType.RELOP, 3);
        }
    }

    private Token readRightAngle() throws LexicalException{
        scan.getReader().mark(1);
        char ch = scan.getNextChar();
        if(ch == '=')
            return new Token<>(TokenType.RELOP, 6);
        else{
            scan.getReader().reset();
            return new Token<>(TokenType.RELOP, 4);
        }
    }

    private Token readPlusMinus(char ch) {
        TokenType type = prevToken.getType();

        if (type == TokenType.RIGHTPAREN || type == TokenType.RIGHTBRACKET || type == TokenType.IDENTIFIER ||
                type == TokenType.INTCONSTANT || type == TokenType.REALCONSTANT)
            return new Token<>(TokenType.ADDOP, ch == '+' ? 1 : 2);
        else
            return new Token<>(ch == '+' ? TokenType.UNARYPLUS : TokenType.UNARYMINUS); //TODO could also be a constant

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
                return new Token<>(TokenType.RIGHTPAREN);
            case ')':
                return new Token<>(TokenType.LEFTPAREN);
            case '[':
                return new Token<>(TokenType.LEFTBRACKET);
            case ']':
                return new Token<>(TokenType.RIGHTBRACKET);
            case '=':
                return new Token<>(TokenType.RELOP, 1)
            default:
                throw new LexicalException("Invalid Character");
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
        table.put("INTEGER", TokenType.INTCONSTANT);
        table.put("REAL", TokenType.REALCONSTANT);
        table.put("ARRAY", TokenType.ARRAY);
        table.put("OF", TokenType.OF);
        table.put("NOT", TokenType.NOT);
        table.put("IF", TokenType.IF);
        table.put("THEN", TokenType.THEN);
        table.put("ELSE", TokenType.ELSE);
        table.put("WHILE", TokenType.WHILE);
        table.put("DO", TokenType.DO);
        table.put("DIV", TokenType.MULOP);
        table.put("MOD", TokenType.MULOP);
        table.put("AND", TokenType.MULOP);
        table.put("OR", TokenType.ADDOP);
    }
}


