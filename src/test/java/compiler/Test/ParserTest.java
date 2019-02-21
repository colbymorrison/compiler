package compiler.Test;

import compiler.Exception.CompilerError;
import compiler.Lexer.Lexer;
import compiler.Parser.Parser;
import org.junit.jupiter.api.Test;

import java.io.File;

class ParserTest {
    /**
     * Tests the source files first for parser specific test files
     * Then for general test files.
     *
     * @throws CompilerError
     */
    @Test
    void testFiles() throws CompilerError {
        String resPath = "src/test/resources/";
        testParser(new File(resPath + "Parser"));
        testParser(new File(resPath + "Code"));
//        testParser(new File(resPath + "Now"));
    }


    private void testParser(File dir) throws CompilerError {
        for (File f : dir.listFiles()) {
            System.out.println(f.getAbsolutePath());
            Lexer lexer = new Lexer(f.getAbsolutePath());
            Parser parser = new Parser(lexer, false);
            parser.parse();
        }
    }


}