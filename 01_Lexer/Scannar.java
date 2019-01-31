import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.nio.CharBuffer;

public class Scannar {
    private static final String VALID_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890" +
                    ".,;:<>/*[]+-=()}{\t ";
    private final int blockSize = 10;
    // Pointers to buffer, array is [index, bufferNo]
    private int[] lexemeBegin = new int[]{0, 0};
    private int[] forward = new int[]{1, 0};
    private CharBuffer[] buffers;
    private InputStream stream;
    private int lineNumber = 0;
    private int linePos = 0;

    public Scannar(String fileName) throws LexicalException, IOException {
        try {
            this.stream = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            throw new LexicalException("This file does not exist");
        }

        buffers = new CharBuffer[]{CharBuffer.allocate(blockSize + 1), CharBuffer.allocate(blockSize + 1)};
        reloadBuffer(buffers[0]);

    }

    // Reloads buffer from input stream
    private void reloadBuffer(CharBuffer buf) throws IOException {
        byte[] bytes = new byte[blockSize];
        int read = stream.read(bytes);
        if (read == -1)
            return;

        String text = new String(bytes);
        buf.put(text.toCharArray());
        buf.put(blockSize, (char) 3);
    }


    // Gets the next char from a buffer and reloads buffer if we're at the end
    public char getNextChar(boolean isLexeme) throws LexicalException, IOException {
        char ch;
        // Get array for correct variable, position 0 is index in buffer,
        // position 1 is current buffer
        int[] idxArr = isLexeme ? lexemeBegin : forward;
        int idx = idxArr[0]++;
        CharBuffer buf = buffers[idxArr[1]];


        // Get next character from buffer and increment
        ch = buf.get(idx);

        // Is this character valid?
        if ()
            throw new LexicalException("Invalid Character");

        switch (ch) {
            // This is eof,
            case (char) 3:
                // If we're at the end of a buffer, move to other buffer
                if (idx == blockSize) {
                    int bufferPlus = (idxArr[1] + 1) % 2;
                    idxArr[1] = bufferPlus;

                    // If its a lexeme
                    if (isLexeme) {
                        idxArr[0] = idx + 1;
                        lexemeBegin = idxArr;
                    } else {
                        idxArr[0] = 0;
                        reloadBuffer(buffers[bufferPlus]);
                        forward = idxArr;
                    }

                    break;
                } else {
                    return (char) 2;
                }
                //white space: comments, spaces, tabs, carriage returns, and newlines
            case 'w':
                //TODO
                break;
            case VALID_CHARS.indexOf(ch) == -1:

            default:
                if (isLexeme)
                    lexemeBegin = idxArr;
                else
                    forward = idxArr;
                break;
        }
        return ch;
    }

    public static void main(String[] args) {
        Scannar s = null;
        try {
            s = new Scannar("/Users/Colby/jr/Compilers/project/Test/test.txt");
            System.out.println(s.getNextChar(true));

            for (int i = 0; i < 13; i++)
                System.out.println(s.getNextChar(false));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
