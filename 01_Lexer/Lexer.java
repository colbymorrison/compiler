import java.io.FileInputStream;
import java.nio.CharBuffer;

public class Lexer{
    private static final int blockSize = 4096;
    private GetChar charStream;

    public Lexer(FileInputStream stream){
        // Initialize buffers
        charStream = new GetChar(stream);


        charStream.initBuffer(buf0, buf1);

    }

    public Token getNextToken() throws LexicalException{
        char forward = getChar
        char c = getChar();
    }


}