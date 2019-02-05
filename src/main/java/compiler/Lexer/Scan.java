package compiler.Lexer;

import java.io.*;

import compiler.Exception.LexerError;

public class Scan {
    private static final String VALID_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890" +
                    ".,;:<>/*[]+-=()}{\t\n ";
    private final int blockSize = 4096;
    private int col = 0;
    private int row = 1;
    private BufferedReader reader;

    public Scan(String fileName) throws LexerError, IOException {
        try {
            reader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            throw LexerError.ioError(e.getMessage());
        }
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public BufferedReader getReader() {
        return reader;
    }

    private void updateLines(char ch) {
        if (ch == '\n') {
            col = 0;
            row++;
        }
    }


    // Gets the next char from a buffer and reloads buffer if we're at the end
    public char getNextChar() throws LexerError, IOException {
        int read = reader.read();
        char ch = (char) read;
        col++;
        //If -1, we've reached the end of the file
        if (read == -1) {
            ch = (char) 3;
            reader.close();
        } else if (!VALID_CHARS.contains(Character.toString(ch)))
            throw LexerError.invalidCharacter(ch, row, col);
        updateLines(ch);
        // We have a comment
        if (ch == '{') {
            readComments();
            // Return whitespace for comment
            ch = ' ';
        }
        // Case is not signifigant
        return Character.toUpperCase(ch);
    }

    private void readComments() throws LexerError, IOException {
        char ch;
        do {
            ch = (char) reader.read();
            char lookahead = ch;
            if (ch == '}') {
                reader.mark(1);
                ch = (char) reader.read();
                if (ch == '}')
                    throw LexerError.invalidCharacter('}', row, col);
                else {
                    reader.reset();
                    ch = lookahead;
                }
            }
            updateLines(ch);
        } while (ch != '}');
    }

}
