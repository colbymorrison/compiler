package compiler;

import compiler.Exception.CompilerError;
import compiler.Lexer.Lexer;
import compiler.Parser.Parser;

/**
 * Main wrapper class for the whole compiler
 */
class Compiler {
    private final Parser parser;

    /**
     * Create a parser with a lexer that uses the file path
     *
     * @param filePath file path to compile
     * @param debug    should parser/semantic action debug info be printed?
     */
    private Compiler(String filePath, boolean debug) {
        this.parser = new Parser(new Lexer(filePath), debug);
    }

    /**
     * Start the compiler by starting the parser
     */
    private void compile() {
        try {
            System.out.println(parser.parse());
        } catch (CompilerError e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Entry point for the program, create an start the compiler
     *
     * @param args command line args, first entry should be file to compile
     *             if second entry is --debug or -d, debug mode is turned on
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Enter the path of the file to compile as a command line argument");
            return;
        }

        // Is debug mode on?
        boolean debug = args.length >= 2 && (args[1].equals("--debug") || args[1].equals("-d"));

        // Go!
        Compiler compiler = new Compiler(args[0], debug);
        compiler.compile();
    }
}
