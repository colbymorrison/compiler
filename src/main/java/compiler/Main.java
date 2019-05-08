package compiler;

import java.io.FileNotFoundException;

public class Main
{
    /**
     * Entry point for the program, create an start the compiler
     *
     * @param args command line args, first entry should be file to Compile
     *             if second entry is --debug or -d, debug mode is turned on
     */
    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.out.println("Error: no input file provided.");
            System.out.println("Usage: compiler file [--debug | -d]");
            return;
        }

        // Is debug mode on?
        boolean debug = args.length >= 2 && (args[1].equals("--debug") || args[1].equals("-d"));

        // Compile the file if it exists
        try
        {
            Compiler compiler = new Compiler(args[0], debug);
            compiler.Compile();
        } catch (FileNotFoundException e)
        {
            System.out.println("The file " + args[0] + " does not exist. Try again.");
        }
    }
}
