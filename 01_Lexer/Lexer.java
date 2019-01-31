import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;

public class Lexer {
    private Scan scan;
    // This should be done in the symbol table, but we haven't made that yet
    private Hashtable<String, TokenType> table = new Hashtable<>();

    public Lexer(String s) throws LexicalException, IOException {
        // Initialize buffers
        scan = new Scan(s);
        initTable();
    }

    public Token getNextToken() throws LexicalException {
        Token token = null;
        char c = scan.getNextChar();
        if (c == Token.WHITESPACE)
            c = scan.getNextChar();
        if (Character.isLetter(c))
            token = readIdentifier(c);
        else if (Character.isDigit(c))
            token = readDigit(c);
        else if (c == '=' || c == '<' || c == '>')
            token = readRelOp(c);
        else if (c == '+' || c == '-') {
        }
        // TODO handle
        else
            token = readSymbol(c);

        return token;
    }

    private Token readIdentifier(char ch) throws LexicalException {
        StringBuilder buffer = new StringBuilder();
        Token tok;

        while (Character.isDigit(ch) || Character.isLetter(ch)) {
            buffer.append(ch);
            ch = scan.getNextChar();
        }

        // TODO
//        if(!(ch == WHITESPACE))
//            pushBack;

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
        } else {
            return new Token<>(TokenType.IDENTIFIER);
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


}