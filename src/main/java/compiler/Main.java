package compiler;

import compiler.Exception.ParserError;
import compiler.Lexer.Lexer;
import compiler.Parser.Parser;

class Main {
    /**
     * Driver to run compiler at this stage
     *
     * @param args command line args, first entry should be file to compile
     *             if second entry is --debug or -d, debug mode is turned on
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Enter the path of the file to compile as a command line argument");
            return;
        }
        Lexer lexer = new Lexer(args[0]);
        Parser parser;

        if (args.length >= 2 && (args[1].equals("--debug") || args[1].equals("-d")))
            parser = new Parser(lexer, true);
        else
            parser = new Parser(lexer, false);

        try {
            parser.parse();
        } catch (ParserError e) {
            System.out.println(e.getMessage());
        }
    }
}
