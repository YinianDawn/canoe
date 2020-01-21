package canoe.parser;

import canoe.lexer.Token;
import canoe.util.PanicUtil;

/**
 * @author dawn
 */
public class Channel {

    private String name;

    private TokenStream stream;

    public Channel(String name, TokenStream stream) {
        this.name = name;
        this.stream = stream;
    }

    // stream 的同名方法

    boolean has(int number) { return stream.has(number); }

    boolean has() { return has(1); }

    int getPosition() { return stream.getPosition(); }

    void move(int position) {
        stream.move(position);
    }

    void mark() { stream.mark(); }

    void recover() { stream.recover(); }

    Token next(boolean move) { return stream.next(move); }

    Token next() { return next(true); }

    Token nextSkipSpaceOrCR() { return stream.nextSkipSpaceOrCR(); }

    Token nextSkipSpace() { return stream.nextSkipSpace(); }

    Token current() { return stream.current(); }

    void removeSpace() { while (has() && next(false).isSpaces()) { next(); } }

    void removeCR() { while (has() && next(false).isCR()) { next(); } }

    void removeSpaceOrCR() { while (has() && next(false).isSpacesOrCR()) { next(); } }


    protected void panic(String tip) {
        PanicUtil.panic(tip, name, stream.current());
    }

    protected void panic(String tip, Token token) {
        PanicUtil.panic(tip, name, token);
    }

}
