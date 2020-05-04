package canoe.parser;


import canoe.lexer.Kind;
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

    TokenStream(Tokens tokens) {
        this.originTokens = tokens;
        // 移除注释
        List<Token> ots = tokens.getTokens().stream().filter(t -> !t.isComment()).collect(Collectors.toList());

        List<Token> ts = new LinkedList<>();
        Token last = null;
        int size = ots.size();
        for (int i = 0; i < size; i++) {
            Token token = ots.get(i);
            if (token.isCR()) {
                // 换行后面的空格或换行无用 只保留第一个换行
                while (i < size - 1 && ots.get(i + 1).isSpacesSemiCR()) { i++; }
            } else if (token.isSpaces()) {
                if (null != last && last.isSpaces()) {
                    // 上一个是空格 本次空格不需要
                    continue;
                } else if (i < size - 1) {
                    if (ots.get(i + 1).isCR() || ots.get(i + 1).isSemi()) {
                        // 换行或分号前的空格 不需要
                        continue;
                    }
                }
            }
            ts.add(token);
            last = token;
        }

        // 如果第一个是空格换行分号无用 移除
        while (0 < ts.size() && ts.get(0).isSpacesSemiCR()) { ts = ts.subList(1, ts.size()); }
        this.tokens = new ArrayList<>(ts);
        this.size = this.tokens.size();
    }


    public int position() { return position; }

    public void move(int position) {
        this.position = position;
    }

    public void mark() { marks.push(position); }

    public void recover() { move(marks.pop()); }

    public void forget() { marks.pop(); }


    public boolean has() { return position + 1 < size; }

    public Token next() { position++; return tokens.get(position); }

    public Token glance() { return tokens.get(position + 1); }

    public boolean guess(Kind kind) { return has() && glance().is(kind); }


    public Token glanceSkipSpacesCR() {
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

    public Token glanceSkipSpaces() {
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


    public void dropSpaces() { while (has() && glance().isSpaces()) { next(); } }
    public void dropCR() { while (has() && glance().isCR()) { next(); } }
    public void dropSemi() { while (has() && glance().isSemi()) { next(); } }
    public void dropSpacesCR() { while (has() && glance().isSpacesCR()) { next(); } }
    public void dropSpacesSemiCR() { while (has() && glance().isSpacesSemiCR()) { next(); } }

    public void drop(Kind kind) { while (has() && glance().is(kind)) { next(); } }
    public void drop(Kind kind, Kind kind2) { while (has() && glance().is(kind, kind2)) { next(); } }


    public Tokens getOriginTokens() {
        return originTokens;
    }

    public String name() {
        return originTokens.getSourceFile().getName();
    }
}
