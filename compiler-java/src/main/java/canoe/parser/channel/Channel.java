package canoe.parser.channel;

import canoe.ast.expression.Expression;
import canoe.ast.expression.ExpressionComma;
import canoe.ast.expression.ExpressionTrait;
import canoe.ast.merge.Merge;
import canoe.ast.statement.Statement;
import canoe.ast.statement.StatementComma;
import canoe.ast.statement.StatementEmpty;
import canoe.ast.statement.Statements;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.TokenStream;
import canoe.parser.channel.statement.StatementChannel;
import canoe.util.PanicException;
import canoe.util.PanicUtil;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static canoe.lexer.KindSet.SINGLE_KEY_WORDS;

/**
 * @author dawn
 */
public class Channel<T> {

    private String name;
    private TokenStream stream;
    private ConcurrentLinkedDeque<Object> channel = new ConcurrentLinkedDeque<>();
    private Set<Kind> end;
    protected T data;

    private Set<Kind> ignore = new HashSet<>();
    private HashMap<Kind, Supplier<Boolean>> match = new HashMap<>();
    private Runnable full;
    private Set<Kind> accept = new HashSet<>();
    private Set<Kind> refuse = new HashSet<>();
    private boolean acceptAll = false;
    private boolean refuseAll = false;

    public Channel(Channel channel, Kind... end) {
        this(channel.name, channel.stream, end);
    }

    public Channel(String name, TokenStream stream, Kind... end) {
        this.name = name;
        this.stream = stream;
        this.end = new HashSet<>(Arrays.asList(end));
        this.end.add(Kind.EOF);
    }

    /**
     * 每个子类可以继承这个方法用于准备工作
     * @return
     */
    protected void init() { while (hunger()) { eat(); } }

    /**
     * 判断这个通道是否结束
     * @return
     */
    private boolean hunger() { return null == data; }

    /**
     * 每个子类必须继承这个方法用以移进元素
     * @return
     */
    private void eat() {
        Token next = glance();

        if (ignore.contains(next.kind)) { next(); eat(); return; }

        if (match.containsKey(next.kind) && match.get(next.kind).get()) {
            return;
        } else if (null != full && end(next)){
            full.run();
            return;
        }

        if (!pass(next) && !eat(next)) { return; }

        addLast(next());

        clear();

        digest();

        digested();
    }

    // ==================== 忽略符号 ====================

    public Channel ignore(Kind... kinds) { Collections.addAll(ignore, kinds); return this; }

    public Channel ignoreSpace() { ignore.add(Kind.SPACES); return this; }

    // ==================== 某种特殊符号需要做的操作 ====================

    public void match(Kind kind, Supplier<Boolean> end) { match.put(kind, end); }

    // ==================== 遇到终结符做的操作 并且结束通道 ====================

    public void over(Runnable full) { this.full = full; }

    // ==================== 接受和拒绝的符号 ====================

    public Channel accept(Kind... kinds) { if (!acceptAll) { Collections.addAll(accept, kinds); } return this; }
    public Channel acceptKeyWords() { if (!acceptAll) { accept.addAll(SINGLE_KEY_WORDS); } return this; }
    public Channel acceptAll() { acceptAll = true; return this; }
    public Channel acceptSpaces() { if (!acceptAll) { accept.add(Kind.SPACES); } return this; }
    public Channel acceptCR() { if (!acceptAll) { accept.add(Kind.CR); } return this; }
    public Channel acceptColon() { if (!acceptAll) { accept.add(Kind.COLON); } return this; }

    public Channel refuse(Kind... kinds) { if (!refuseAll) { Collections.addAll(refuse, kinds); } return this; }
    public Channel refuseAll() { refuseAll = true; return this; }
    public Channel refuseSpaces() { if (!refuseAll) { refuse.add(Kind.SPACES); } return this; }
    public Channel refuseCR() { if (!refuseAll) { refuse.add(Kind.CR); } return this; }

    private boolean pass(Token next) {
        if (acceptAll || accept.contains(next.kind)) { return true; }
        if (refuseAll || refuse.contains(next.kind)) { panic(next); }
        return false;
    }

    // ==================== 每个通道可以自己决定是否接受某个字符 ====================

    protected boolean eat(Token next) { return true; }

    // ==================== 已经接受新的字符了，上次的状态相关信息要清除 ====================

    private void clear() {
        ignore.clear();
        accept.clear();
        refuse.clear();
        acceptAll = false;
        refuseAll = false;
        match.clear();
        full = null;
    }

    protected void digest() { panic("should override this method"); }

    protected void digested() { }

    // ==================== 获取这个通道计算的结果 ====================

    protected T produce() {
        if (hunger()) {
            panic("channel is hungering, can not produce.");
        }
        return data;
    }


    // ==================== 结束后是否移除末尾符号 ====================

    private void removeEnds(Kind[] kinds) {
        if (hunger()) {
            panic("channel is hungering, can not remove end sign.");
        }
        if (null == kinds || 0 == kinds.length) {
            while (glance().not(end)) { next(); }
        } else {
            while (glance().not(kinds)) { next(); }
        }
    }

    protected void removeEnd(Kind... kinds) {
        removeEnds(kinds);
    }

    public Channel tryRemoveSpace() {
        if (!end(Kind.SPACES)) {
            removeSpace();
        }
        return this;
    }

    // ================ 其他方法 ================


    // ================ 拓展终结符号 ================

    protected Kind[] extend(Kind... kinds) {
        if (0 == kinds.length) { return new ArrayList<>(end).toArray(new Kind[]{}); }
        return Stream.concat(end.stream(), Stream.of(kinds)).distinct().collect(Collectors.toList()).toArray(new Kind[]{});
    }

    // ================ 按照终结符号判断是否结束 ================

    protected boolean end(Token next) {
        return end.contains(next.kind);
    }

    protected boolean end(Kind kind) {
        return end.contains(kind);
    }

    protected boolean end() {
        return end(glanceSkipSpace());
    }

    // ================ 当前通道状态 ================

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

    // ================ 解析一个对象的识别Id ================

    protected String parseName(Object o) {
        if (o instanceof Expression
                || o instanceof Statement
                || o instanceof Statements
                || o instanceof ExpressionTrait
                || o instanceof Merge) {
            return o.getClass().getSimpleName();
        } else if (o instanceof Token) {
            return ((Token) o).kind.name();
        }
        panic("can not parse to a name: " + o);
        return "";
    }

    // ================ 解析多个语句 ================

    protected Statements parseStatements(Kind... end) {
        ConcurrentLinkedDeque<Statement> statements = new ConcurrentLinkedDeque<>();
        // 解析多个语句
        removeSpaceOrCR();
        List<Token> commas = new LinkedList<>();
        Statement statement = StatementChannel.produce(this, end);
        while (!(statement instanceof StatementEmpty)) {
            statements.add(statement);
            if (glanceSkipSpaceOrCR().is(Kind.COMMA) || !commas.isEmpty()) {
                removeSpace();
                if (glance().is(Kind.COMMA)) {
                    commas.add(next());
                    removeSpace();
                }
            } else {
                removeSpaceOrCR();
            }
            statement = StatementChannel.produce(this, end);
        }
        if (commas.isEmpty() || statements.size() <= 1) {
            return new Statements(new ArrayList<>(statements));
        } else {
            if (commas.size() + 1 != statements.size()) {
                panic("statements and , can not match.");
            }
            Statement first = statements.removeFirst();
            List<ExpressionComma> commaList = new ArrayList<>(commas.size());
            for (Token comma : commas) {
                commaList.add(new ExpressionComma(comma, statements.removeFirst()));
            }
            return new Statements(Collections.singletonList(new StatementComma(first, commaList)));
        }
    }

    private void tryRemoveSpaceOrCR(Kind[] ends) {
        if (contains(ends, Kind.SPACES)) {
            if (contains(ends, Kind.CR)) { } else { remove(Kind.CR); }
        } else {
            if (contains(ends, Kind.CR)) { removeSpace(); } else { removeSpaceOrCR(); }
        }
    }

    private static boolean contains(Kind[] ends, Kind kind) {
        for (Kind k : ends) {
            if (k == kind) {
                return true;
            }
        }
        return false;
    }

    protected static HashMap<String, Integer> PRIORITY = new HashMap<>(97);

    static {
        int p = 1;

        // ( ) [ ] -> 后缀运算符 从左到右
        /** (   */ PRIORITY.put(Kind.LR.sign, p);
        /** [   */ PRIORITY.put(Kind.LS.sign, p);
        /** {   */ // PRIORITY.put(Kind.LB.sign, p);
        /** )   */ // PRIORITY.put(Kind.LR.sign, p);
        /** ]   */ // PRIORITY.put(Kind.LS.sign, p);
        /** }   */ // PRIORITY.put(Kind.LB.sign, p);

        p++;
        /** .   */ PRIORITY.put(Kind.DOT.sign, p);
        /** ..  */ PRIORITY.put(Kind.DOT_DOT.sign, p);
        /** ... */ PRIORITY.put(Kind.DOT_DOT_DOT.sign, p);

        p++;
        // ! + - ++ -- 单目运算符 从右到左
        /** !   */ PRIORITY.put(Kind.BIT_NOT.sign, p);
        /** +l  */ PRIORITY.put(Kind.ADD.sign + "l", p);
        /** -l  */ PRIORITY.put(Kind.SUB.sign + "l", p);
        /** ++  */ PRIORITY.put(Kind.ADD_ADD.sign, p);
        /** --  */ PRIORITY.put(Kind.SUB_SUB.sign, p);

        p++;
        // * / % 双目运算符 从左到右
        /** *   */ PRIORITY.put(Kind.MUL.sign, p);
        /** /   */ PRIORITY.put(Kind.DIV.sign, p);
        /** %   */ PRIORITY.put(Kind.MOD.sign, p);

        p++;
        // + - 双目运算符 从左到右
        /** +   */ PRIORITY.put(Kind.ADD.sign, p);
        /** -   */ PRIORITY.put(Kind.SUB.sign, p);

        p++;
        // >> << 位移运算符 双目 从左到右
        /** >>  */ PRIORITY.put(Kind.BIT_RIGHT.sign, p);
        /** <<  */ PRIORITY.put(Kind.BIT_LEFT.sign, p);

        p++;
        // < <= > >= 关系运算符 双目 从左到右
        /** >   */ PRIORITY.put(Kind.GT.sign, p);
        /** >=  */ PRIORITY.put(Kind.GE.sign, p);
        /** <   */ PRIORITY.put(Kind.LT.sign, p);
        /** <=  */ PRIORITY.put(Kind.LE.sign, p);

        /** ->  */ PRIORITY.put(Kind.IS.sign, p);

        p++;
        // == != 关系运算符 双目 从左到右
        /** ==  */ PRIORITY.put(Kind.EQ.sign, p);
        /** !=  */ PRIORITY.put(Kind.NE.sign, p);

        p++;
        // & 按位与 双目 从左到右
        /** &   */ PRIORITY.put(Kind.BIT_AND.sign, p);

        p++;
        // ^ 按位异或 双目 从左到右
        /** ^   */ PRIORITY.put(Kind.BIT_XOR.sign, p);

        p++;
        // | 按位或 双目 从左到右
        /** |   */ PRIORITY.put(Kind.BIT_OR.sign, p);

        p++;
        // && 逻辑与 双目 从左到右
        /** &&  */ PRIORITY.put(Kind.LOGICAL_AND.sign, p);

        p++;
        // || 逻辑或 双目 从左到右
        /** ||  */ PRIORITY.put(Kind.LOGICAL_OR.sign, p);

        p++;
        /** :   */ PRIORITY.put(Kind.COLON.sign, p);

        p++;
        // , 运算 双目 从左到右
        /** ,   */ PRIORITY.put(Kind.COMMA.sign, p);

        p++;
        // := = += -= /= %= >>= <<= &= |= 赋值运算 双目 从右到左
        /** :=  */ PRIORITY.put(Kind.ASSIGN_FORCE.sign, p);
        /** =   */ PRIORITY.put(Kind.ASSIGN.sign, p);
        /** +=  */ PRIORITY.put(Kind.ADD_ASSIGN.sign, p);
        /** -=  */ PRIORITY.put(Kind.SUB_ASSIGN.sign, p);
        /** *=  */ PRIORITY.put(Kind.MUL_ASSIGN.sign, p);
        /** /=  */ PRIORITY.put(Kind.DIV_ASSIGN.sign, p);
        /** %=  */ PRIORITY.put(Kind.MOD_ASSIGN.sign, p);
        /** <<= */ PRIORITY.put(Kind.BIT_LEFT_ASSIGN.sign, p);
        /** >>= */ PRIORITY.put(Kind.BIT_RIGHT_ASSIGN.sign, p);
        /** &=  */ PRIORITY.put(Kind.BIT_AND_ASSIGN.sign, p);
        /** ^=  */ PRIORITY.put(Kind.BIT_XOR_ASSIGN.sign, p);
        /** |=  */ PRIORITY.put(Kind.BIT_OR_ASSIGN.sign, p);

        p++;
        /** ->  */ PRIORITY.put(Kind.LAMBDA.sign, p);

        p++;
        /** ;   */ PRIORITY.put(Kind.SEMI.sign, p);

    }

    protected boolean priority(Token self, Token other, String leftOtherSign) {
        Integer p1;
        if (null == leftOtherSign) {
            p1 = PRIORITY.get(self.kind.sign);
        } else {
            p1 = PRIORITY.get(self.kind.sign + leftOtherSign);
        }
        if (null == p1) {
            panic("not a operator sign.", self);
            return false;
        }
        if (null == other.kind.sign) { return true; }
        Integer p2 = PRIORITY.get(other.kind.sign);
        if (null == p2) { return true; }
        return p1 <= p2;
    }

    protected boolean priority(Token self, Token other) {
        return this.priority(self, other, null);
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

    protected boolean channelSize(int size) {
        return channel.size() == size;
    }

    protected boolean channelSizeLess(int size) {
        return channel.size() < size;
    }

    protected boolean isChannelEmpty() {
        return channel.isEmpty();
    }

    protected boolean isChannelFull() {
        return !channel.isEmpty();
    }

    // ================ 工具方法 ================

    protected static boolean ends(String status, String... ends) {
        for (String end : ends) {
            if (status.endsWith(end)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean contains(Token token, Set<Kind> set) {
        return set.contains(token.kind);
    }

    protected static boolean contains(Token token, Set<Kind> set, Set<Kind> set2) {
        return contains(token, set) || contains(token, set2);
    }

    public static boolean contains(Token token, Set<Kind> set, Set<Kind> set2, Set<Kind> set3) {
        return contains(token, set) || contains(token, set2) || contains(token, set3);
    }

    public static boolean contains(Token token, Set<Kind> set, Set<Kind> set2, Set<Kind> set3, Set<Kind> set4) {
        return contains(token, set) || contains(token, set2) || contains(token, set3) || contains(token, set4);
    }

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

    public void remove(Kind kind) { stream.remove(kind); }

}
