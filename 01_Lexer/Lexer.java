import java.io.FileInputStream;
import java.nio.CharBuffer;

public class Lexer{
    private static final int blockSize = 4096;
    private CharBuffer buf0;
    private CharBuffer buf1;
    private GetChar charStream;

    public Lexer(FileInputStream stream){
        // Initialize buffers
        charStream = new GetChar(stream);
        buf0 = CharBuffer.allocate(blockSize + 1);
        buf1 = CharBuffer.allocate(blockSize + 1);

        charStream.initBuffer(buf0, buf1);

    }


}