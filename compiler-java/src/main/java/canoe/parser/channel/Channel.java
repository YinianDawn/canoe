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

import static canoe.lexer.KindSet.COMMON_KEY_WORDS;

/**
 * @author dawn
 */
public class Channel<T> {

    private String name;
    private TokenStream stream;
    private ConcurrentLinkedDeque<Object> channel = new ConcurrentLinkedDeque<>();
    private Set<Kind> accept = new HashSet<>();
    private Set<Kind> refuse = new HashSet<>();
    private boolean acceptAll = false;
    private boolean refuseAll = false;
    private Set<Kind> end;
    private Runnable full;
    protected T data;

    public Channel(Channel channel, Kind... end) {
        this(channel.getName(), channel.getStream(), end);
    }

    public Channel(String name, TokenStream stream, Kind... end) {
        this.name = name;
        this.stream = stream;
        this.end = new HashSet<>(Arrays.asList(end));
        this.end.add(Kind.CR);
        this.end.add(Kind.EOF);
    }

    /**
     * 每个子类可以继承这个方法用于准备工作
     * @return
     */
    protected void init() {
        panic("should override this method");
    }

    /**
     * 每个子类必须继承这个方法判断这个通道是否结束
     * @return
     */
    protected boolean hunger() {
        panic("should override this method");
        return false;
    }

    /**
     * 每个子类必须继承这个方法用以移进元素
     * @return
     */
    protected void eat() {
        panic("should override this method");
    }

    /**
     * 每个子类必须继承这个方法用以归约
     * @return
     */
    protected void digest() {
        panic("should override this method");
    }

    /**
     * 如果遇到结束符调用
     * @return
     */
    protected void full() { }

    /**
     * 获取这个通道计算的结果
     * @return
     */
    protected T produce() {
        if (hunger()) {
            panic("channel is hungering, can not produce.");
        }
        return data;
    }

    // ================ 一些工具方法 ================

    /**
     * 拓展终结符号
     * @param kinds
     * @return
     */
    protected Kind[] extend(Kind... kinds) {
        return Stream.concat(end.stream(), Stream.of(kinds)).distinct().collect(Collectors.toList()).toArray(new Kind[]{});
    }

    /**
     * 按照终结符号判断是否结束
     * @param next
     * @return
     */
    protected boolean end(Token next) {
        return end.contains(next.kind);
    }

    protected boolean end() {
        return end(glanceSkipSpace());
    }

    /**
     * 统一判断是否拒绝或接受
     * @param next
     * @return
     */
    protected boolean pass(Token next) {
        if (acceptAll || accept.contains(next.kind)) {
            return true;
        }
        if (refuseAll || refuse.contains(next.kind)) {
            panic(next);
        }
        return false;
    }

    /**
     * 统一判断是否拒绝或接受
     * @param next
     * @return
     */
    protected boolean over(Token next) {
        if (null != full && end(next)) {
            full.run();
            return true;
        }
        return false;
    }

    /**
     * 清除接受或拒绝类型
     */
    protected void clear() {
        accept.clear();
        refuse.clear();
        acceptAll = false;
        refuseAll = false;
        full = null;
    }

    public void over(Runnable full) {
        this.full = full;
        if (!acceptAll) {
            accept.addAll(end);
        }
    }

    /**
     * 下个字符接受什么类型
     * @param kinds
     * @return
     */
    public Channel accept(Kind... kinds) {
        if (acceptAll) { return this; }
        Collections.addAll(accept, kinds);
        return this;
    }

    public Channel acceptKeyWords() {
        if (acceptAll) { return this; }
        accept.addAll(COMMON_KEY_WORDS);
        return this;
    }

    public Channel acceptAll() {
        acceptAll = true;
        return this;
    }

    public Channel acceptSpaces() {
        if (acceptAll) { return this; }
        accept.add(Kind.SPACES);
        return this;
    }

    public Channel acceptCR() {
        if (acceptAll) { return this; }
        accept.add(Kind.CR);
        return this;
    }

    /**
     * 下个字符拒绝什么类型
     * @param kinds
     * @return
     */
    public Channel refuse(Kind... kinds) {
        if (refuseAll) { return this; }
        Collections.addAll(refuse, kinds);
        return this;
    }

    public Channel refuseAll() {
        refuseAll = true;
        return this;
    }

    public Channel refuseSpaces() {
        if (refuseAll) { return this; }
        refuse.add(Kind.SPACES);
        return this;
    }

    public Channel refuseCR() {
        if (refuseAll) { return this; }
        refuse.add(Kind.CR);
        return this;
    }

    /**
     * 当前通道状态
     * @return
     */
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

    /**
     * 解析一个对象的识别Id
     * @param o
     * @return
     */
    protected String parseID(Object o) {
        if (o instanceof Expression
                || o instanceof Statement
                || o instanceof Merge) {
            return o.getClass().getSimpleName();
        } else if (o instanceof Token) {
            return ((Token) o).kind.name();
        }
        panic("can not parse to ID: " + o);
        return "";
    }


    // ================ channel方法 ================

    protected void addLast(Object e) { channel.addLast(e); }

    protected Object removeLast() { return channel.removeLast(); }

    protected Object getLast() { return channel.getLast(); }

    protected Token getLastToken() {
        if (channel.isEmpty()) { return null; }
        Object o = getLast();
        if (o instanceof Token) {
            return (Token) o;
        }
        return null;
    }

    protected Object removeFirst() { return channel.removeFirst(); }

    protected Object getFirst() { return channel.getFirst(); }

    protected int channelSize() {
        return channel.size();
    }

    protected boolean isChannelEmpty() {
        return channel.isEmpty();
    }

    protected boolean channelFull() {
        return !channel.isEmpty();
    }

    // ================ 通用 ================

    public String getName() { return name; }

    public TokenStream getStream() { return stream; }

    protected void panic(String tip) throws PanicException {
        PanicUtil.panic(tip, name, stream.current());
    }

    protected void panic(String tip, Token token) throws PanicException {
        PanicUtil.panic(tip, name, token);
    }

    protected void panic(Token token) {
        panic(token.kind.name() + " is refused here.", token);
    }

    // ================ stream 的同名方法 ================

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

}
