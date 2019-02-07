package compiler.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import compiler.Lexer.*;
import compiler.Exception.LexerError;

import java.io.*;
import java.util.ArrayList;

/**
 * Tests for lexer. We compare against provided test files and time the lexer.
 */
class LexerTest {
    private static final String PATH = "src/test/resources/Lexer/";
    private final ArrayList<Long> timing = new ArrayList<>();

    // Set up one test per test file
    @Test
    void testFile1() {
        testAgainstFile(1);
    }

    @Test
    void testFile2() {
        testAgainstFile(2);
    }

    @Test
    void testFile3() {
        testAgainstFile(3);
    }

    @Test
    void testFile4() {
        testAgainstFile(4);
    }

    @Test
    void testFile5() {
        testAgainstFile(5);
    }

    /**
     * Tests lexer against test file
     *
     * @param fileNo the test file no to test against
     */
    private void testAgainstFile(int fileNo) {
        ArrayList<String> tokens = new ArrayList<>();
        ArrayList<String> out;
        long startTime = 0;
        long endTime = 0;

        // Run lexer on test file into tokens List
        try {
            Lexer l = new Lexer(PATH + "in/lextest_" + fileNo + ".txt");
            Token tok;
            do {
                startTime = System.nanoTime();
                tok = l.getNextToken();
                endTime = System.nanoTime();
                tokens.add(tok.toString());
            } while (tok.getType() != TokenType.ENDOFFILE);
        } catch (LexerError e) {
            System.out.println(e.getMessage());
        }
        timing.add(endTime - startTime);

        // Read text file into out list
        try {
            out = readLines(PATH + "out/lexsoln_" + fileNo + ".txt");
            // Compare the lists
            assertArrayEquals(tokens.toArray(), out.toArray());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // Calculate the time to read all tokens
        long totalTime = timing.stream().mapToLong(x -> x).sum();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(PATH + "timing.txt", true));
            writer.write("Testfile " + fileNo + ": " + totalTime + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads test file into a list of token strings
     */
    static ArrayList<String> readLines(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        ArrayList<String> lines = new ArrayList<>();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            if (line.indexOf('[') != 0) continue;
            lines.add(line);
        }

        bufferedReader.close();
        return lines;
    }

}