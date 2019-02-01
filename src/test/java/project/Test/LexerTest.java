package project.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import project.Lexer.*;
import project.Exception.LexicalException;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class LexerTest {
    static final String PATH = "src/test/resources/Lexer/";

   @Test
     void getNextToken(){
       ArrayList<String> tokens =  new ArrayList<>();
        try {
            Lexer l = new Lexer(PATH + "in/lextest_1.txt");
            for(int i = 0; i < 3; i++)
                tokens.add(l.getNextToken().toString());
            ArrayList<String> out = readLines(PATH + "out/lexsoln_1.txt");
            assertArrayEquals(tokens.toArray(), out.toArray());
        }
        catch(LexicalException|IOException ioe){
           ioe.printStackTrace();
        }
    }

    static ArrayList<String> readLines(String filename) throws IOException{
        FileReader fileReader = new FileReader(filename);

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        ArrayList<String> lines = new ArrayList<>();
        String line = null;

        while ((line = bufferedReader.readLine()) != null)
        {
            lines.add(line);
        }

        bufferedReader.close();
        return lines;
    }

}