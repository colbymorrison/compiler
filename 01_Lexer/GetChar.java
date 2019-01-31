import java.io.*;
import java.nio.CharBuffer;

public class GetChar {
    private static final int blockSize = 4096;
    private int lexeme = 0;
    private int forward = 1;
    private CharBuffer buf0;
    private CharBuffer buf1;
    private InputStream stream;
    private int lineNumber = 0;
    private int linePos = 0;

    public GetChar(String fileName) throws LexicalException, IOException{
        try {
            this.stream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            throw new LexicalException("This file does not exist");
        }
        buf0 = CharBuffer.allocate(blockSize + 1);
        buf1 = CharBuffer.allocate(blockSize + 1);
        reloadBuffer(buf0);

    }

    // Reloads buffer from input stream
    private void reloadBuffer(CharBuffer buf) throws IOException {
        byte[] bytes = new byte[4096];
        int read = stream.read(bytes);
        if (read == -1)
            return;

        String text = new String(bytes);
        buf.put(text.toCharArray());
        buf.put(blockSize, (char) 3);
    }


    // Gets the next char from a buffer and reloads buffer if we're at the end
    public char getNextChar(boolean isLexeme) throws IOException {
        // We're looking for the next lexeme
        if(isLexeme){
            lexeme++;
            forward = lexeme + 1;
        }
        char ch = buf.get();
        if ((int) ch == 3) {
            reloadBuffer(buf);
            return buf.get();
        } else return buf.get();

    }

}
