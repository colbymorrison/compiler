package compiler.Lexer;

import java.io.*;

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
     */
    Scan(String fileName) {
        try {
            reader = new BufferedReader(new FileReader(fileName));
        } // If the file doesn't exist, there's not much we can do
        catch (FileNotFoundException e) {
            System.out.println("The file " + fileName + "does not exist. Try again.");
            System.exit(1);
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
