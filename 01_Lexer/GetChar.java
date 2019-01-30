import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.CharBuffer;

public class GetChar{
    private static final int blockSize = 4096;
    private InputStream stream;

    public GetChar(FileInputStream stream){
        this.stream = stream;
    }

    private void reloadBuffer(CharBuffer buf) throws IOException{
        byte[] bytes = new byte[4096];
        int read = stream.read(bytes);
        if(read == -1)
            return;

        String text = new String(bytes);
        buf.put(text.toCharArray());
        buf.put(blockSize, (char) 3);
    }


    public void initBuffer(CharBuffer buf0, CharBuffer buf1) throws IOException{
        reloadBuffer(buf0);
        reloadBuffer(buf1);
    }

    public char getNextChar(CharBuffer buf) throws IOException{
        char ch = buf.get();
        if((int) ch == 3) {
            reloadBuffer(buf);
            return buf.get();
        }
        else return buf.get();

    }

}
