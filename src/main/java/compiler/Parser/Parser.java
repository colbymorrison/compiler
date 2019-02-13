package compiler.Parser;
import compiler.Exception.CompilerError;
import compiler.Lexer.Lexer;
import compiler.Lexer.Token;
import compiler.Lexer.TokenType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {
    private static final int NUMPRODS = 67;
    private static final int TBLROWS = 35;
    private static final int TBLCOLS = 39;
    private final String EPSILON = "*E*";
    private List<String> productions = new ArrayList<>();
    private List<String> nTerms = new ArrayList<>();
    private List<String> terms = new ArrayList<>();
    private HashMap<Integer, Integer>[] parseTbl = new HashMap[TBLROWS];
    private Stack<String> stack = new Stack<>();
    private Lexer lexer;

    //TODO do much better error handling
    public Parser(Lexer lexer) {
        this.lexer = lexer;
        try {
            initTables();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void dumpStack(String top, String inType, boolean mth){
        String out = "";
        out += "Stack " + stack + "\nPopped " + top + " with token " + inType + "\n";
        if(mth)
            out += "Match! \n";
        else
            out += "Pushing \n";
        System.out.println(out);
    }

    private void parse() throws CompilerError{
        stack.push(TokenType.ENDOFFILE.toString());
        stack.push("<Goal>");
        Token input = lexer.getNextToken();
        // Get the next token, if it's EOF, stop
        while(input.getType() != TokenType.ENDOFFILE){
            // Get the type as a string as the stack is of Strings
            String inType = input.getType().name();
            String top = stack.pop();
            // If it's a terminal, the input token must be the same
            if(isTerminal(top)) {
                if (!inType.equals(top))
                    throw new CompilerError("Bad string");
                else {
                    input = lexer.getNextToken();
                    dumpStack(top, inType, true);
                }
            }
            else{
                // If it's a non-terminal, get the parse table row for the token
                // By looking up the index in the list of terminals
                HashMap<Integer,Integer> tblRow = parseTbl[terms.indexOf(inType)];
                // Get the parse table column by looking into the list of non-terminals
                int colIdx = nTerms.indexOf(top) - 1;
                // If there's a mapping to an entry, its valid
                if(tblRow.containsKey(colIdx)){
                   // The production rule is the entry in the parse table
                   Integer prodIdx = tblRow.get(colIdx);
                   // Check for epsilon transition
                   if(prodIdx < 0)
                       continue;
                   String[] rules = productions.get(prodIdx).split(" ");
                   // Check for epsilon rule
                   if(rules[0].equals(""))
                       continue;
                   // Push rules in reverse order
                   for(int i = rules.length - 1; i >= 0; i--)
                       stack.push(rules[i]);
                   dumpStack(top, inType, false);
                }
                else
                    throw new CompilerError("Error state");
            }
        }
    }

    private boolean isTerminal(String s){
        return Stream.of(TokenType.values()).anyMatch(x -> x.name().equals(s));
    }

    private void initTables() throws IOException {
        Path path = Paths.get("src", "main", "resources");
        productions.add(null);
        try (Stream<String> stream = Files.lines(path.resolve("grammar.txt"))) {
            productions.addAll(stream.collect(Collectors.toList()));
        }

        String[] lines;
        try (Stream<String> stream = Files.lines(path.resolve("parsetable.txt"))) {
            lines = stream.toArray(String[]::new);
        }

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            parseTbl[i] = new HashMap<>();
            String[] elts = line.split(",");
            for (int j = 0; j < elts.length; j++) {
                int elt = Integer.parseInt(elts[j]);
                if (elt != 999)
                    parseTbl[i].put(j, elt);
            }
        }

        try (Stream<String> stream = Files.lines(path.resolve("parseidx.txt"))) {
            List<String> list = stream.collect(Collectors.toList());
            nTerms = list.subList(0, TBLCOLS);
            terms = list.subList(TBLCOLS, list.size());
        }
    }


    //TODO testfile instead of just running
    public static void main(String[] args){
        try {
            Lexer lexer = new Lexer("src/test/resources/parser/ex1.txt");
            Parser parser = new Parser(lexer);
            parser.parse();
        }
        catch(CompilerError e){
            e.printStackTrace();
        }

    }

}
