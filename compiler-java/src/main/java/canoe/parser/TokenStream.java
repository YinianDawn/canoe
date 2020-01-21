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
public class TokenStream {

    private final Tokens originTokens;
    private final List<Token> tokens;
    private int position = -1;
    private int size;

    private Stack<Integer> marks = new Stack<>();

    public TokenStream(Tokens tokens) {
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

    public boolean has(int number) { return position + number < size; }

    public boolean has() { return has(1); }

    public int getPosition() { return position; }

    public void move(int position) {
        this.position = position;
    }

    public void mark() { marks.push(position); }

    public void recover() { move(marks.pop()); }

    public void forget() { marks.pop(); }


    public Token next() { position++; return tokens.get(position); }

    public Token glance() { return tokens.get(position + 1); }

    public Token glanceSkipSpaceOrCR() {
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

    public Token glanceSkipSpace() {
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

    public Token current() {
        if (position < 0 || size <= position) {
            panic("wrong position: " + position);
        }
        return tokens.get(position);
    }

    public void removeSpace() { while (has() && glance().isSpaces()) { next(); } }

    public void removeSpaceOrCR() { while (has() && glance().isSpacesOrCR()) { next(); } }

}
