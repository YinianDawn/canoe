package canoe.util;

import canoe.lexis.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author dawn
 */
public class Util {

    public static void panic(String tip) {
        panic(tip, null, null);
    }

    public static void panic(String tip, String fileName, Token token) {
        show(tip, fileName, token);
        throw new Error(tip);
    }

    private static void show(String tip, String fileName, Token token) {
        if (null == fileName || null == token) { return; }

        int range = 3;
        int line = token.getLine();
        int index = token.getIndex();
        int length = token.getLength();

        try (FileReader fileReader = new FileReader(fileName);
             BufferedReader reader = new BufferedReader(fileReader)) {
            int no = 0;
            String content;
            while (null != (content = reader.readLine())) {
                no++;
                if (Math.abs(line - no) < range) {
                    System.out.println(no + " | " + content);
                    if (no == line) {
                        // 当前行 红色加箭头
                        StringBuilder sb = new StringBuilder();
                        int blanks = String.valueOf(line).length();
                        for (int i = 0; i < blanks; i++) {
                            sb.append(' ');
                        }
                        sb.append(" | ");

                        for (int i = 0; i < index - 1; i++) {
                            sb.append('\t' == content.charAt(i) ? '\t' : ' ');
                        }
                        int max = length;
                        if (content.length() + 1 < index + max) {
                            max = content.length() + 1 - index;
                        }
                        for (int i = 0; i < max; i++) {
                            sb.append('↑');
                        }
                        sb.append(' ').append(tip);
                        System.err.println(sb.toString());
                    }
                }
                if (line + range < no) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
