package compiler.Parser;

import compiler.Exception.CompilerError;
import compiler.Lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.io.File;

class ParserTest {
    @Test
    void testFiles() throws CompilerError {
        File dir = new File("src/test/resources/Code");
        File[] files = dir.listFiles();
        for (File f : files) {
            System.out.println(f.getAbsolutePath());
            testParser(f.getAbsolutePath());
        }
    }

    private void testParser(String inFile) throws CompilerError {
        Lexer lexer = new Lexer(inFile);
        Parser parser = new Parser(lexer, false);
        parser.parse();
    }


}