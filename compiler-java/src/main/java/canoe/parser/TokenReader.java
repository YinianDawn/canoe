package canoe.parser;

import canoe.lexis.Kind;
import canoe.lexis.Token;
import canoe.lexis.Tokens;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dawn
 */
class TokenReader {

    private final Tokens originTokens;
    private final List<Token> tokens;
    private int index = -1;
    private int size;

    TokenReader(Tokens tokens) {
        this.originTokens = tokens;

        // 移除注释
        List<Token> ots = tokens.getTokens().stream().filter(t -> !t.comment()).collect(Collectors.toList());
        List<Token> ts = new LinkedList<>();

        int size = ots.size();
        for (int i = 0; i < size; i++) {
            Token token = ots.get(i);
            if (token.isCR()) {
                // 多个连续换行只保留1个
                while (i < size - 1 && ots.get(i + 1).isCR()) {
                    i++;
                }
            }
            if (token.isSpaces()) {
                if (token.getIndex() == 1) {
                    // 行首空格不需要
                    continue;
                } else if (i < size - 1 && ots.get(i + 1).isCR()) {
                    // 行尾空格不需要
                    continue;
                }
            }
            ts.add(token);
        }

        while (0 < ts.size() && ts.get(0).getKind() == Kind.CR) {
            ts = ts.subList(1, ts.size());
        }
        this.tokens = new ArrayList<>(ts);
        this.size = this.tokens.size();
    }

    boolean hasNext() {
        return index < size;
    }

    Token nextToken() {
        return nextToken(true);
    }

    Token nextToken(boolean move) {
        if (move) {
            index++;
            return tokens.get(index);
        } else {
            return tokens.get(index + 1);
        }
    }

    Token thisToken() {
        if (index < 0 || size <= index) {
            return null;
        }
        return tokens.get(index);
    }

    String getFileName() {
        return originTokens.getFileName();
    }

    List<Token> getRemainTokens() {
        return tokens.subList(index + 1, size);
    }

    public int getIndex() {
        return index;
    }

    void move(int index) {
        this.index = index;
    }
}
