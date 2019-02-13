package compiler.Parser;

import compiler.Exception.CompilerError;
import compiler.Lexer.Lexer;
import compiler.Lexer.Token;
import compiler.Lexer.TokenType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {
    private static final int NUMPRODS = 67;
    private static final int TBLROWS = 35;
    private static final int TBLCOLS = 39;
    private final String EPSILON = "*E*";
    private ArrayList<String>[] productions = new ArrayList[NUMPRODS];
    private List<String> nTerms = new ArrayList<>();
    private List<String> terms = new ArrayList<>();
    private HashMap<Integer, Integer>[] parseTbl = new HashMap[TBLROWS];
    private Stack<String> stack = new Stack<>();
    private Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        try {
            initTables();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parse() throws CompilerError{
        stack.push(TokenType.ENDOFFILE.toString());
        stack.push("<Goal>");

        while(true){
            Token input = lexer.getNextToken();
            String inType = input.getType().name();
            String top = stack.pop();
            System.out.println("Popped " + top + " with token " + inType);
            if(isTerminal(top)) {
                if (!inType.equals(top))
                    throw new CompilerError("Bad string");
            }
            else{
                HashMap<Integer,Integer> tblEntry = parseTbl[terms.indexOf(inType)];
                // IS THIS RIGHT??
                int idx = nTerms.indexOf(top) - 1;
                if(tblEntry.containsKey(idx)){
                   Integer prodIdx = tblEntry.get(idx) - 1;
                   ArrayList<String> production = productions[prodIdx];
                   Collections.reverse(production);
                   production.forEach(rule -> stack.push(rule));
                    System.out.println("Stack "+stack);
                }
                else
                    throw new CompilerError("Bad");
            }
        }
    }


    private boolean isTerminal(String s){
        return Stream.of(TokenType.values()).anyMatch(x -> x.name().equals(s));
    }

    private void initTables() throws IOException {
        productions[0] = null;
        String[] lines = readFile("grammar.txt");
        //TODO replace with streams??
        ArrayList<String> production = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            String[] split = lines[i].split("::= ");
            if (split.length == 2) {
                production.add(split[1]);
                productions[i] = production;
                production = new ArrayList<>();
            }
            else
                production.add(EPSILON);
        }

        lines = readFile("parsetable.txt");
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

        try (Stream<String> stream = Files.lines(Paths.get("src", "main", "resources", "parseidx.txt"))) {
            List<String> list = stream.collect(Collectors.toList());
            nTerms = list.subList(0, TBLCOLS - 1);
            terms = list.subList(TBLCOLS, list.size());
        }
    }

    private String[] readFile(String path) throws IOException{
        try (Stream<String> stream = Files.lines(Paths.get("src", "main", "resources", path))) {
            return stream.toArray(String[]::new);
        }
    }

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
