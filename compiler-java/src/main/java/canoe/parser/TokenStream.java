package canoe.parser;


import canoe.lexer.Token;
import canoe.lexer.Tokens;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
class TokenStream {

    private final Tokens originTokens;
    private final List<Token> tokens;
    private int position = -1;
    private int size;

    private Stack<Integer> marks = new Stack<>();

    TokenStream(Tokens tokens) {
        this.originTokens = tokens;

        // 移除注释
        List<Token> ots = tokens.getTokens().stream().filter(t -> !t.isComment()).collect(Collectors.toList());

        List<Token> ts = new LinkedList<>();
        int size = ots.size();
        for (int i = 0; i < size; i++) {
            Token token = ots.get(i);
            if (token.isCR()) {
                // 换行后面的空格或换行无用 只保留第一个换行
                while (i < size - 1 && ots.get(i + 1).isSpacesOrCR()) { i++; }
            }
            if (token.isSpaces()) {
                // 行首空格不需要
                if (token.position == 1) { continue; }
                // 行尾前空格不需要
                if (i < size - 1 && ots.get(i + 1).isCR()) { continue; }
            }
            ts.add(token);
        }

        // 如果第一个是换行移除
        while (0 < ts.size() && ts.get(0).isCR()) { ts = ts.subList(1, ts.size()); }
        this.tokens = new ArrayList<>(ts);
        this.size = this.tokens.size();
    }

    boolean has(int number) { return position + number < size; }

    boolean has() { return has(1); }

    public int getPosition() { return position; }

    void move(int position) {
        this.position = position;
    }

    void mark() { marks.push(position); }

    void recover() { move(marks.pop()); }

    Token next(boolean move) {
        if (move) {
            position++;
            return tokens.get(position);
        } else {
            return tokens.get(position + 1);
        }
    }

    Token next() { return next(true); }


    Token nextSkipSpaceOrCR() {
        int position = this.position + 1;
        Token token = tokens.get(position);
        while (token.isSpaces() || token.isCR()) {
            position++;
            if (size <= position) {
                panic("no more useful token");
            }
            token = tokens.get(position);
        }
        return token;
    }

    Token nextSkipSpace() {
        int position = this.position + 1;
        Token token = tokens.get(position);
        while (token.isSpaces()) {
            position++;
            if (size <= position) {
                panic("no more useful token");
            }
            token = tokens.get(position);
        }
        return token;
    }

    Token current() {
        if (position < 0 || size <= position) {
            panic("wrong position: " + position);
        }
        return tokens.get(position);
    }

    void removeSpace() { while (has() && next(false).isSpaces()) { next(); } }

    void removeCR() { while (has() && next(false).isCR()) { next(); } }

    void removeSpaceOrCR() { while (has() && next(false).isSpacesOrCR()) { next(); } }

    String getFileName() { return originTokens.getSourceFile().getName(); }

    public List<Token> getRemainTokens() { return tokens.subList(position + 1, size); }

}
