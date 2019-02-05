package project.Lexer;

import java.io.*;
import project.Exception.LexicalException;

public class Scan {
    private static final String VALID_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890" +
                    ".,;:<>/*[]+-=()}{\t\n ";
    private final int blockSize = 4096;
    // TODO: make these right
    private int col = 0;
    private int row = 1;
    private BufferedReader reader;

    public Scan(String fileName) throws LexicalException, IOException {
        try {
            reader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            throw LexicalException.ioError(e.getMessage());
        }
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public BufferedReader getReader() {
        return reader;
    }

    private void updateLines(char ch){
        if(ch == '\n'){
            col = 0;
            row++;
        }
    }


    // Gets the next char from a buffer and reloads buffer if we're at the end
    public char getNextChar() throws LexicalException, IOException {
        int read = reader.read();
        char ch = (char) read;
        col++;
        //If -1, we've reached the end of the file
        if (read == -1) {
            ch = (char) 3;
            reader.close();
        } else if (!VALID_CHARS.contains(Character.toString(ch)))
            throw LexicalException.invalidCharacter(ch, row, col);
        updateLines(ch);
        // Comment TODO: look over specification
        if (ch == '{') {
            do {
                ch = (char) reader.read();
                updateLines(ch);
            } while (ch != '}');
            // Read 1 past '}'
            ch = (char) reader.read();
            updateLines(ch);
        }
        return Character.toUpperCase(ch);
    }

}
