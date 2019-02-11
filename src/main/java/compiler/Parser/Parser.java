package compiler.Parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
    private static final String resPath = "src/main/resources";
    private static final int GRAMLINES = 67;
    private static final int TBLROWS = 35;
    private static final int TBLCOLS = 38;
    private String[] grammar;
    private int[][] parseTbl;

    public Parser() {
        try {
            initTables();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initTables() throws IOException {
        grammar = new String[GRAMLINES];
        parseTbl = new int[TBLROWS][TBLCOLS];
        BufferedReader reader = new BufferedReader(new FileReader(resPath + "grammar.txt"));
        int count = 0;
        for(int i = 0; i < GRAMLINES; i++){
            grammar[count] = reader.readLine();
        }

        reader = new BufferedReader(new FileReader(resPath + "parsetable.txt"));
        reader.readLine();
        for(int i = 0; i < TBLROWS; i++) {
            String[] line = reader.readLine().split(",", TBLCOLS-1);
            for (int j = 0; j < TBLCOLS; j++)
                parseTbl[i][j] = Integer.parseInt(line[j]);
        }
    }
}
