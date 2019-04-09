//package compiler.Test;
//
//import compiler.Exception.CompilerError;
//import compiler.Exception.SemanticError;
//import compiler.Lexer.Lexer;
//import compiler.Parser.Parser;
//import org.junit.jupiter.api.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//import static org.junit.jupiter.api.Assertions.assertArrayEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//class ParserTest {
//    private final String RESPATH = "src/test/resources/";
//
//    @Test
//    void testSemAct2() throws CompilerError, IOException {
//        List<String> passing = new ArrayList<>();
//        passing.add("phase2-6_ns.vas");
//        passing.add("phase2-1_ns.vas");
//        testParserHard(RESPATH + "SemAct2", passing);
//    }
//
//    @Test
//    void testSemAct3() throws CompilerError, IOException {
//        List<String> passing = new ArrayList<>();
//        for (int i = 1; i <= 4; i++) {
//            passing.add("phase3-" + i + ".vas");
//        }
//        passing.add("phase3-7.vas");
//        passing.add("phase3-8.vas");
//        testParserHard(RESPATH + "SemAct3", passing);
//    }
//
//    /**
//     * Tests parser for correct opcodes
//     */
//    void testParserHard(String path, List<String> passing) throws CompilerError, IOException {
//        for (File f : Objects.requireNonNull(new File(path + "/in").listFiles())) {
//            String name = f.getName();
//            System.out.println("---------------------------");
//            System.out.println(name);
//            if(!name.equals("phase3-8.vas"))
//                continue;
//            // If the file shouldn't throw an error, check it against correct file
//            if (passing.contains(name)) {
//                String[] generatedCodes = getCodes(f);
//
//                // Read test file to get codes to check against
//                String fileName = f.getName().split(".vas")[0];
//                Path testPath = Paths.get(path, "out", fileName + ".tvi");
//                String[] testCodes = Files.lines(testPath).toArray(String[]::new);
//
//                assertArrayEquals(generatedCodes, testCodes);
//            }
//            // Others throw an error
//            else {
//                try {
//                    getCodes(f);
//                } catch (SemanticError e) {
//                    System.out.println(e.getMessage());
//                }
//                assertThrows(SemanticError.class, () -> getCodes(f));
//            }
//        }
//    }
//
//    /**
//     * Tests parser on parser specific and general test files
//     * Fails only if an error is thrown
//     */
//    @Test
//    void testParserLight() throws CompilerError {
//        testParser(new File(RESPATH + "Parser"));
//        testParser(new File(RESPATH + "Code"));
//    }
//
//    // Runs parser and returns generated intermediate code
//    private String[] getCodes(File f) throws CompilerError {
//        Lexer lexer = new Lexer(f.getAbsolutePath());
//        Parser parser = new Parser(lexer, false);
//        String intCode = parser.parse();
//        System.out.println(intCode);
//
//        return intCode.split("\n");
//    }
//
//
//    // Runs parser and fails if any errors are thrown
//    private void testParser(File dir) throws CompilerError {
//        for (File f : dir.listFiles()) {
////            if(f.getName().equals("func.vas")) {
//            System.out.println(f.getAbsolutePath());
//            Lexer lexer = new Lexer(f.getAbsolutePath());
//            Parser parser = new Parser(lexer, true);
//            parser.parse();
////            }
//        }
//    }
//
//}