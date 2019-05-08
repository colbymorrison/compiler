package compiler.Lexer;

import java.io.*;

/**
 * This class scans input and returns the next character in the file
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
     * @param file the file to parse
     */
    Scan(File file)
    {
        try
        {
            Reader = new BufferedReader(new FileReader(file));
        } // We've already checked if the file exists, so this error shouldn't get thrown
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
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

    /**
     * Decrements column by a certian amount
     */
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
        // Case is not significant
        return Character.toUpperCase(ch);
    }
}
