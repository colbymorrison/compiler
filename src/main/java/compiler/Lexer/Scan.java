package compiler.Lexer;

import java.io.*;

import compiler.Exception.LexerError;

/**
 * This class scans input and returns the next non-comment character in the file
 * and keeps track of the current row and column.
 */
public class Scan {
    private static final String VALID_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890" +
                                              ".,;:<>/*[]+-=()}{\t\r\n ";
    private int col = 0;
    private int row = 1;
    private BufferedReader reader;

    /**
     * Constructor
     *
     * @param fileName of file to parse
     * @throws LexerError if one occurred
     */
    public Scan(String fileName) throws LexerError {
        try {
            reader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            throw LexerError.ioError(e.getMessage());
        }
    }

    /**
     * Getter for current row of file
     *
     * @return row
     */
    public int getRow() {
        return row;
    }

    /**
     * Getter for current column of file.
     *
     * @return col
     */
    public int getCol() {
        return col;
    }

    /**
     * Reads next character from input and ensures it is valid and not part of a comment
     *
     * @return the next non-comment character from the file
     * @throws LexerError
     * @throws IOException
     */
    public char getNextChar() throws LexerError, IOException {
        int read = reader.read();
        char ch = (char) read;
        col++;
        //If -1, we've reached the end of the file
        if (read == -1) {
            // We're using $ as eof character
            ch = '$';
            reader.close();
            // Ensure character is valid
        } else if (!VALID_CHARS.contains(Character.toString(ch)))
            throw LexerError.invalidCharacter(ch, row, col);

        // Check for newline
        else if (ch == '\n') {
            col = 0;
            row++;
        }
        // We have a comment
        else if (ch == '{') {
            readComment();
            // Return whitespace for comment
            ch = ' ';
        }

        // Case is not signifigant
        return Character.toUpperCase(ch);
    }

    /**
     * Reads through a comment and ensures it is valid.
     */
    private void readComment() throws LexerError, IOException {
        char ch;
        do{
            ch = (char) reader.read();
            if (ch == '}') {
                // Lookahead one character because we can't have }} in a comment
                reader.mark(1);
                ch = (char) reader.read();
                if (ch == '}')
                    throw LexerError.invalidComment(row, col);
                else {
                    reader.reset();
                    return;
                }
            }
            // Check for newline
            if (ch == '\n') {
                col = 0;
                row++;
            }
        } while (true);
    }

}
