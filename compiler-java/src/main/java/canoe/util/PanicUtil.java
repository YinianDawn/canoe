package canoe.util;

import canoe.lexer.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static canoe.util.PrintUtil.print;

/**
 * @author dawn
 */
public class PanicUtil {


    public static void panic(String panic) throws PanicException {
        throw new PanicException(panic);
    }

    public static void panic(String panic, Token token, String name) throws PanicException {
        show(panic, token, name);
//        System.exit(0);
        throw new PanicException(panic + (null != token ? " token: " + token.toString() : ""));
    }

    private static void show(String panic, Token token, String name) {
        if (null == name || null == token) { return; }

        int range = 3;
        int line = token.line;
        int position = token.position;
        int size = token.size;

        try (FileReader fileReader = new FileReader(name);
             BufferedReader reader = new BufferedReader(fileReader)) {
            int no = 0;
            String content;
            while (null != (content = reader.readLine())) {
                no++;
                if (Math.abs(line - no) < range) {
                    print(no + " | " + content);
                    if (no == line) {
                        // 当前行 红色加箭头
                        StringBuilder sb = new StringBuilder();
                        int blanks = String.valueOf(line).length();
                        for (int i = 0; i < blanks; i++) {
                            sb.append(' ');
                        }
                        sb.append(" | ");

                        for (int i = 0; i < position - 1; i++) {
                            sb.append('\t' == content.charAt(i) ? '\t' : ' ');
                        }
                        int max = size;
                        if (content.length() + 2 < position + max) {
                            max = content.length() + 2 - position;
                        }
                        if (max <= 0) { max = 1; }
                        for (int i = 0; i < max; i++) {
                            sb.append('↑');
                        }
                        sb.append(' ').append(panic);
                        print(sb.toString(), true);
                    }
                }
                if (line + range < no) { break; }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
