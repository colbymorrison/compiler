package compiler.Lexer;

import java.io.*;

/**
 * This class scans input and returns the next non-comment character in the file
 * and keeps track of the current Row and Column.
 */
class Scan
{
    private int Col = 0;
    private int Row = 1;

    private BufferedReader Reader;

    /**
     * Constructor
     *
     * @param fileName of file to parse
     */
    Scan(String fileName)
    {
        try
        {
            Reader = new BufferedReader(new FileReader(fileName));
        } // If the file doesn't exist, there's not much we can do
        catch (FileNotFoundException e)
        {
            System.out.println("The file " + fileName + " does not exist. Try again.");
            System.exit(1);
        }
    }

    /**
     * Getter for current Row of file
     *
     * @return Row
     */
    int GetRow()
    {
        return Row;
    }

    void SetMinCol(int col)
    {
        this.Col -= col;
    }

    /**
     * Getter for current column of file.
     *
     * @return col
     */
    int GetCol()
    {
        return Col;
    }

    /**
     * Reads next character from input and ensures it is valid and not part of a comment
     *
     * @return the next non-comment character from the file
     */
    char GetNextChar() throws IOException
    {
        int read = Reader.read();
        char ch = (char) read;
        Col++;
        //If -1, we've reached the end of the file
        if (read == -1)
        {
            // We're using $ as eof character
            ch = '$';
            Reader.close();
        }

        // Check for newline
        else if (ch == '\n')
        {
            Col = 0;
            Row++;
        }
        // Case is not signifigant
        return Character.toUpperCase(ch);
    }
}
