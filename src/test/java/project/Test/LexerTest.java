package project.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import project.Lexer.*;
import project.Exception.LexerError;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class LexerTest {
    private static final String PATH = "src/test/resources/Lexer/";

    @Test
    void testFile1() {
        getNextToken(1);
    }

    @Test
    void testFile2() {
        getNextToken(2);
    }

    @Test
    void testFile3() {
        getNextToken(3);
    }

    @Test
    void testFile4() {
        getNextToken(4);
    }

    @Test
    void testFile5() {
        getNextToken(5);
    }

    // Tests lexer against provided testfiles
    private void getNextToken(int fileNo) {
        ArrayList<String> tokens = new ArrayList<>();
        ArrayList<String> out;

        // Run lexer on testfile into tokens List
        try {
            Lexer l = new Lexer(PATH + "in/lextest_" + fileNo + ".txt");
            Token tok;
            do {
                long startTime = System.nanoTime();
                tok = l.getNextToken();
                long endTime = System.nanoTime();
                tokens.add(tok.toString());
            } while (tok.getType() != TokenType.ENDOFFILE);
        } catch (LexerError | IOException e) {
            e.printStackTrace();
           // System.out.println(e);
        }

        // Read textfile into out list
        try {
            out = readLines(PATH + "out/lexsoln_" + fileNo + ".txt");
            // Compare the lists
            assertArrayEquals(tokens.toArray(), out.toArray());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    // Reads testfile into a list of token strings
    static ArrayList<String> readLines(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        ArrayList<String> lines = new ArrayList<>();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            if(line.indexOf('[') != 0) continue;
            lines.add(line);
        }

        bufferedReader.close();
        return lines;
    }

}