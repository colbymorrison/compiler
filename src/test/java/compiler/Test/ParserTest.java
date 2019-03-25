package compiler.Test;

import compiler.Exception.CompilerError;
import compiler.Lexer.Lexer;
import compiler.Parser.Parser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ParserTest {

    /**
     * Tests parser for correct opcodes
     */
    @Test
    void testParserHard() throws CompilerError, IOException {
        String resPath = "src/test/resources/";

        String basePath = resPath + "SemAct2";
        for (File f : Objects.requireNonNull(new File(basePath + "/in").listFiles())) {
            if(f.getName().equals("phase2-1_ns.vas" )) {
                System.out.println("---------------------------");
                System.out.println(f.getName());

                String[] generatedCodes = getCodes(f);

                // Read test file to get codes to check against
                String fileName = f.getName().split(".vas")[0];
                Path testPath = Paths.get(basePath, "out", fileName+".tvi");
                String[] testCodes = Files.lines(testPath).toArray(String[]::new);

                assertArrayEquals(generatedCodes, testCodes);
            }
        }
    }

    // Runs parser and returns generated intermediate code
    private String[] getCodes(File f) throws CompilerError, IOException {
        Lexer lexer = new Lexer(f.getAbsolutePath());
        Parser parser = new Parser(lexer, false);
        String intCode = parser.parse();
        System.out.println(intCode);

        return intCode.split("\n");
    }


    /**
     * Tests parser on parser specific and general test files
     * Fails only if an error is thrown
     */
    @Test
    void testParserLight() throws CompilerError{
        String resPath = "src/test/resources/";
        testParser(new File(resPath + "Parser"));
        testParser(new File(resPath + "Code"));
    }

    // Runs parser and fails if any errors are thrown
    private void testParser(File dir) throws CompilerError {
        for (File f : dir.listFiles()) {
//            if(f.getName().equals("func.vas")) {
            System.out.println(f.getAbsolutePath());
            Lexer lexer = new Lexer(f.getAbsolutePath());
            Parser parser = new Parser(lexer, true);
            parser.parse();
//            }
        }
    }

}