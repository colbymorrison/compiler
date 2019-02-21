package compiler.Exception;

import compiler.Lexer.Token;

import java.util.List;

public class ParserError extends CompilerError {
    public ParserError(List<Token> tokens) {
        super(getMessage(tokens));
    }

    private static String getMessage(List<Token> tokens) {
        StringBuilder builder = new StringBuilder("Compiler Error: \n");
        for (Token token : tokens) {
            builder.append("At line ").append(token.getRow()).append(", character ").append(token.getCol()).append("\n");
        }
        return builder.toString();
    }
}
