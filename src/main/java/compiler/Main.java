package compiler;

import compiler.Exception.ParserError;
import compiler.Lexer.Lexer;
import compiler.Parser.Parser;

class Main {
    /**
     * Driver to run compiler at this stage
     *
     * @param args command line args, first entry should be file to compile
     */
    public static void main(String[] args) {
        Lexer lexer = new Lexer(args[0]);
        Parser parser = new Parser(lexer, true);
        try {
            parser.parse();
        } catch (ParserError e) {
            System.out.println(e.getMessage());
        }
    }
}
