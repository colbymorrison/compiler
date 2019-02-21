package compiler.Lexer;

import java.io.*;

import compiler.Exception.LexerError;

/**
 * This class scans input and returns the next non-comment character in the file
 * and keeps track of the current row and column.
 */
class Scan {

    private int col = 0;
    private int row = 1;
    private BufferedReader reader;

    /**
     * Constructor
     *
     * @param fileName of file to parse
     * @throws LexerError if one occurred
     */
    Scan(String fileName) throws LexerError {
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
    int getRow() {
        return row;
    }

    void setMinCol(int col) {
        this.col -= col;
    }

    /**
     * Getter for current column of file.
     *
     * @return col
     */
    int getCol() {
        return col;
    }

    /**
     * Reads next character from input and ensures it is valid and not part of a comment
     *
     * @return the next non-comment character from the file
     * @throws LexerError
     * @throws IOException
     */
    char getNextChar() throws IOException {
        int read = reader.read();
        char ch = (char) read;
        col++;
        //If -1, we've reached the end of the file
        if (read == -1) {
            // We're using $ as eof character
            ch = '$';
            reader.close();
            // Ensure character is valid
        }

        // Check for newline
        else if (ch == '\n') {
            col = 0;
            row++;
        }
        // Case is not signifigant
        return Character.toUpperCase(ch);
    }
}
