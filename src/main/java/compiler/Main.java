package compiler;

import java.io.File;

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

        File file = new File(args[0]);

        // Check if the file exists
        if (!file.exists())
        {
            System.out.println("The file \""+ args[0] + "\" does not exist. Try again.");
            return;
        }

        // Compile the file!
        Compiler compiler = new Compiler(file, debug);
        compiler.Compile();
    }
}
