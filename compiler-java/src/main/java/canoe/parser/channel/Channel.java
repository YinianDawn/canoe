package canoe.parser.channel;

import canoe.ast.expression.Expression;
import canoe.ast.merge.Merge;
import canoe.ast.statement.Statement;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.TokenStream;
import canoe.util.PanicException;
import canoe.util.PanicUtil;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dawn
 */
public class Channel {

    private String name;

    private TokenStream stream;

    protected ConcurrentLinkedDeque<Object> channel = new ConcurrentLinkedDeque<>();

    protected boolean space = false;
    protected boolean cr = false;

    protected Set<Kind> end;

    public Channel(Channel channel, Kind... end) {
        this(channel.getName(), channel.getStream(), end);
    }

    public Channel(String name, TokenStream stream, Kind... end) {
        this.name = name;
        this.stream = stream;
        this.end = new HashSet<>(Arrays.asList(end));
    }

    // stream 的同名方法

    public boolean has(int number) { return stream.has(number); }

    public boolean has() { return has(1); }

    public int getPosition() { return stream.getPosition(); }

    public void move(int position) {
        stream.move(position);
    }

    public void mark() { stream.mark(); }

    public void recover() { stream.recover(); }

    public void forget() { stream.forget(); }

    public Token next() { return stream.next(); }

    public Token glance() { return stream.glance(); }

    public Token glanceSkipSpaceOrCR() { return stream.glanceSkipSpaceOrCR(); }

    public Token glanceSkipSpace() { return stream.glanceSkipSpace(); }

    public Token current() { return stream.current(); }

    public void removeSpace() { stream.removeSpace(); }

    public void removeSpaceOrCR() { stream.removeSpaceOrCR(); }


    protected Kind[] extend(Kind... kinds) {
        return Stream.concat(end.stream(), Stream.of(kinds)).distinct().collect(Collectors.toList()).toArray(new Kind[]{});
    }

    protected boolean end(Token next) {
        return !end.isEmpty() && end.contains(next.kind);
    }

    protected boolean eatSpaceOrCR(Token next) {
        switch (next.kind) {
            case SPACES:
                if (!space) { panic("can not be blank here.", next); }
                return true;
            case CR:
                if (!cr) { panic("can not be CR(\\ n) here.", next); }
                return true;
            default:
        }
        return false;
    }

    protected void accept(boolean space, boolean cr) {
        this.space = space;
        this.cr = cr;
    }

    protected String status() {
        if (channel.isEmpty()) { return ""; }
        StringBuilder sb = new StringBuilder();
        for (Object o : channel) {
            if (o instanceof Token) {
                sb.append(((Token) o).kind.name()).append(" ");
            } else {
                sb.append(o.getClass().getSimpleName()).append(" ");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    protected String getKind(Object o) {
        if (o instanceof Expression
                || o instanceof Statement
                || o instanceof Merge) {
            return o.getClass().getSimpleName();
        } else if (o instanceof Token) {
            return ((Token) o).kind.name();
        }
        panic("can not be: " + o);
        return "";
    }

    public String getName() {
        return name;
    }

    public TokenStream getStream() {
        return stream;
    }

    protected void panic(String tip) throws PanicException {
        PanicUtil.panic(tip, name, stream.current());
    }

    protected void panic(String tip, Token token) throws PanicException {
        PanicUtil.panic(tip, name, token);
    }

}
