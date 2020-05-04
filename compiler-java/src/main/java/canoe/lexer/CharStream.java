package canoe.lexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
class CharStream {

    /**一次读取固定长度字符 */
    private static final int LENGTH = 4096;

    private FileReader reader;

    private char[] chars = new char[LENGTH];
    private int index = LENGTH - 1;
    private int max = LENGTH;

    CharStream(File file) {
        try {
            this.reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            panic("can not find file: " + file.getName());
        }
    }

    private void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            panic("close file failed.");
        }
    }

    boolean has() {
        // 是否超出范围
        if (max == index + 1) {
            // 上次是否把文件读完
            if (LENGTH == max) {
                try {
                    // 再读取一个字符数组
                    max = reader.read(chars, 0, LENGTH);
                } catch (IOException e) {
                    e.printStackTrace();
                    panic("read file failed.");
                }
                // 如果没有新的内容要关闭文件流
                if (max < LENGTH) { close(); }
                // 如果本次没有读到新的内容
                if (-1 == max) { return false; }
                // 重置到起始位置
                index = -1;
            } else {
                // 已读完 文件结束
                return false;
            }
        }
        return true;
    }

    char next() { index++; return chars[index]; }

    char glance() { return chars[index + 1]; }

    boolean guess(char c) { return has() && glance() == c; }

    boolean guess(char c, char c2) {
        if (has()) {
            char n = glance();
            return n == c || n == c2;
        }
        return false;
    }

    String next4() {
        int number = 4;
        StringBuilder sb = new StringBuilder();
        while (0 < number && has()) {
            sb.append(next());
            number--;
        }
        return sb.toString();
    }

}
