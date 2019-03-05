package compiler.Exception;

import compiler.Lexer.Token;

import java.util.List;

public class ParserError extends CompilerError {
    public ParserError(List<Token> errors) {
        super(getMessage(errors));
    }

    // Adds errors to string
    private static String getMessage(List<Token> errors) {
        StringBuilder builder = new StringBuilder("\n");
        for (Token error : errors) {
            builder.append("Syntax error");
            builder.append(CompilerError.lineMsg(error.getRow(), error.getCol()));
        }
        return builder.toString();
    }
}
