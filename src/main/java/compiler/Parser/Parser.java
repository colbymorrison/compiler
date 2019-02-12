package compiler.Parser;

import compiler.Lexer.Lexer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Parser {
    private static final int NUMPRODS = 67;
    private static final int TBLROWS = 35;
    private static final int TBLCOLS = 39;
    private final String EPSILON = "*E*";
    private ArrayList[] productions = new ArrayList[NUMPRODS];
    private HashMap<Integer, Integer>[] parseTbl = new HashMap[TBLROWS];
    private Lexer lexer;

    public Parser() {
        try {
            initTables();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private parse() {
//        Stack<> ;
//        Token input = lexer.getNextToken();
//    }

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
    }

    private String[] readFile(String path) throws IOException{
        try (Stream<String> stream = Files.lines(Paths.get("src", "main", "resources", path))) {
            return stream.toArray(String[]::new);
        }
    }

}
