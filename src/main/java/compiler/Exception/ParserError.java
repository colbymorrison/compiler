package compiler.Exception;

import java.util.List;

public class ParserError extends CompilerError {
    public ParserError(List<String> errors) {
        super(getMessage(errors));
    }

    // Adds errors to string
    private static String getMessage(List<String> errors) {
        StringBuilder builder = new StringBuilder("Compiler Error: \n");
        errors.forEach(builder::append);
        return builder.toString();
    }
}
