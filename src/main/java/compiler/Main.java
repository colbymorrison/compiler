package compiler;

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
            System.out.println("Enter the path of the file to Compile as a command line argument");
            return;
        }

        // Is debug mode on?
        boolean debug = args.length >= 2 && (args[1].equals("--debug") || args[1].equals("-d"));

        // Go!
        Compiler compiler = new Compiler(args[0], debug);
        compiler.Compile();
    }
}
