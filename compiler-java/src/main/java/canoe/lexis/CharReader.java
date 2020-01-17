package canoe.lexis;

import java.io.*;

import static canoe.util.Util.panic;

/**
 * @author dawn
 */
public class CharReader {

    private File file;
    private FileReader reader;
    private static final int LENGTH = 4096;
    private char[] chars = new char[LENGTH];
    private int index = LENGTH - 1;
    private int max = LENGTH;

    public CharReader(File file) {
        this.file = file;
        try {
            this.reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            panic("can not find file: " + file.getName());
        }
    }

    boolean hasNext() {
        if (max == index + 1) {
            if (LENGTH == max) {
                try {
                    max = reader.read(chars, 0, LENGTH);
                } catch (IOException e) {
                    e.printStackTrace();
                    panic("read file failed.");
                }
                if (-1 == max) {
                    close();
                    return false;
                } else {
                    index = -1;
                }
            } else {
                close();
                return false;
            }
        }
        return true;
    }

    private void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            panic("close file failed.");
        }
    }

    char nextChar() {
        return nextChar(true);
    }

    char nextChar(boolean move) {
        if (move) {
            index = index + 1;
            return chars[index];
        } else {
            return chars[index + 1];
        }
    }

}
