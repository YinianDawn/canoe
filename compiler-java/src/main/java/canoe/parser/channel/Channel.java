package canoe.parser.channel;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.TokenStream;
import canoe.parser.channel.statement.StatementChannel;
import canoe.parser.syntax.Statements;
import canoe.parser.syntax.expression.Expression;
import canoe.parser.syntax.merge.Merge;
import canoe.parser.syntax.statement.Statement;
import canoe.parser.syntax.statement.StatementComma;
import canoe.parser.syntax.statement.StatementEmpty;
import canoe.util.PanicUtil;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dawn
 */
public class Channel<T> {

    private final TokenStream stream;

    private Set<Kind> ends;
    private ConcurrentLinkedDeque<Object> channel = new ConcurrentLinkedDeque<>();
    protected T data;

    /** 每次取token如果是被忽略的，就跳过 */
    private Set<Kind> ignore = new HashSet<>();
    /** 针对该kind是否需要执行什么操作，执行后是否处理这个kind */
    private HashMap<Kind, Supplier<Boolean>> drinks = new HashMap<>();
    /** 如果遇到终结符 是否执行结束操作 */
    private Runnable full;
    /** 接受符号 */
    private Set<Kind> accepts = new HashSet<>();
    /** 拒绝符号 */
    private Set<Kind> refuses = new HashSet<>();
    private boolean acceptAll = false;
    private boolean refuseAll = false;

    public Channel(Channel channel, Kind... ends) {
        this(channel.stream, ends);
    }

    public Channel(TokenStream stream, Kind... ends) {
        this.stream = stream;
        this.ends = new HashSet<>(Arrays.asList(ends));
    }

    protected void init() { while (hunger()) { eat(); } }

    private boolean hunger() { return null == data; }

    private void eat() {
        Token next = glance();

        if (ignore.contains(next.kind)) {
            next();
            eat();
            return;
        }

        if (drinks.containsKey(next.kind) && drinks.get(next.kind).get()) {
            return;
        }

        if (null != full && over(next)){
            full.run();
            return;
        }

        if (!acceptAll && !accepts.contains(next.kind)) {
            if (refuseAll || refuses.contains(next.kind)) {
                panic(next);
            }
        }

        if (!eat(next)) {
            return;
        }

        addLast(next());

        ignore.clear();
        drinks.clear();
        full = null;
        accepts.clear();
        refuses.clear();
        acceptAll = false;
        refuseAll = false;

        digest();

        digested();
    }

    protected boolean over(Token next) { return ends.contains(next.kind); }
    protected boolean over(Kind kind) { return ends.contains(kind); }

    protected boolean eat(Token next) { return true; }
    protected void digest() { panic("should override this method"); }
    protected void digested() { }

    protected T make() {
        if (hunger()) {
            panic("channel is hungering, can not make.");
        }
        return data;
    }

    public Channel ignore(Kind... kinds) { Collections.addAll(ignore, kinds); return this; }
    public Channel ignoreSpaces() { ignore.add(Kind.SPACES); return this; }

    public Channel drink(Kind kind, Supplier<Boolean> end) { drinks.put(kind, end); return this; }
    public Channel over(Runnable full) { this.full = full; return this; }

    public Channel accept(Kind kind) { if (!acceptAll) { accepts.add(kind); } return this; }
    public Channel accept(Kind kind, Kind kind2) { if (!acceptAll) { accepts.add(kind); accepts.add(kind2); } return this; }
    public Channel accept(Kind kind, Kind kind2, Kind kind3) { if (!acceptAll) { accepts.add(kind); accepts.add(kind2); accepts.add(kind3); } return this; }
    public Channel accept(Kind... kinds) { if (!acceptAll) { Collections.addAll(accepts, kinds); } return this; }
    public Channel accept(Collection<Kind> kinds) { if (!acceptAll) { accepts.addAll(kinds); } return this; }
    public Channel acceptAll() { acceptAll = true; return this; }
    public Channel acceptSpaces() { if (!acceptAll) { accepts.add(Kind.SPACES); } return this; }
    public Channel acceptCR() { if (!acceptAll) { accepts.add(Kind.CR); } return this; }
    public Channel acceptColon() { if (!acceptAll) { accepts.add(Kind.COLON); } return this; }

    public Channel refuse(Kind... kinds) { if (!refuseAll) { Collections.addAll(refuses, kinds); } return this; }
    public Channel refuseAll() { refuseAll = true; return this; }
    public Channel refuseSpaces() { if (!refuseAll) { refuses.add(Kind.SPACES); } return this; }
    public Channel refuseCR() { if (!refuseAll) { refuses.add(Kind.CR); } return this; }

    protected Kind[] extend(Kind... kinds) {
        if (0 == kinds.length) { return new ArrayList<>(ends).toArray(new Kind[]{}); }
        return Stream.concat(ends.stream(), Stream.of(kinds)).distinct().collect(Collectors.toList()).toArray(new Kind[]{});
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

    protected String parseName(Object o) {
        if (o instanceof Expression
                || o instanceof Statement
                || o instanceof Statements
                || o instanceof Merge) {
            return o.getClass().getSimpleName();
        } else if (o instanceof Token) {
            return ((Token) o).kind.name();
        }
        panic("can not parse to a name: " + o);
        return "";
    }


    protected Statements parseStatements(Kind... end) {
        ConcurrentLinkedDeque<Statement> statements = new ConcurrentLinkedDeque<>();
        // 解析多个语句
        dropSpacesCR();
        List<Token> commas = new LinkedList<>();
        Statement statement = StatementChannel.make(this, end);
        while (!(statement instanceof StatementEmpty)) {
            statements.add(statement);
            if (glanceSkipSpacesCR().is(Kind.COMMA) || !commas.isEmpty()) {
                dropSpaces();
                if (glance().is(Kind.COMMA)) {
                    commas.add(next());
                    dropSpaces();
                }
            } else {
                dropSpacesCR();
            }
            statement = StatementChannel.make(this, end);
        }
        if (commas.isEmpty() || statements.size() <= 1) {
            return new Statements(new ArrayList<>(statements));
        } else {
            if (commas.size() + 1 != statements.size()) {
                panic("statements and , can not match.");
            }
            return new Statements(Collections.singletonList(new StatementComma(new ArrayList<>(statements), commas)));
        }
    }


//    // ==================== 结束后是否移除末尾符号 ====================
//
//    private void removeEnds(Kind[] kinds) {
//        if (hunger()) {
//            panic("channel is hungering, can not remove end sign.");
//        }
//        if (null == kinds || 0 == kinds.length) {
//            while (glance().not(end)) { next(); }
//        } else {
//            while (glance().not(kinds)) { next(); }
//        }
//    }
//
//    protected void removeEnd(Kind... kinds) {
//        removeEnds(kinds);
//    }
//
//    public Channel tryRemoveSpace() {
//        if (!end(Kind.SPACES)) {
//            removeSpace();
//        }
//        return this;
//    }


//    // ================ 解析多个语句 ================

//    private void tryRemoveSpaceOrCR(Kind[] ends) {
//        if (contains(ends, Kind.SPACES)) {
//            if (contains(ends, Kind.CR)) { } else { remove(Kind.CR); }
//        } else {
//            if (contains(ends, Kind.CR)) { removeSpace(); } else { removeSpaceOrCR(); }
//        }
//    }
//
//    private static boolean contains(Kind[] ends, Kind kind) {
//        for (Kind k : ends) {
//            if (k == kind) {
//                return true;
//            }
//        }
//        return false;
//    }

    protected static HashMap<String, Integer> PRIORITY = new HashMap<>(97);

    static {
        int p = 1;

        // ( ) [ ] -> 后缀运算符 从左到右
        /** (   */ PRIORITY.put(Kind.LR.value, p);
        /** [   */ PRIORITY.put(Kind.LS.value, p);
        /** {   */ // PRIORITY.put(Kind.LB.sign, p);
        /** )   */ // PRIORITY.put(Kind.LR.sign, p);
        /** ]   */ // PRIORITY.put(Kind.LS.sign, p);
        /** }   */ // PRIORITY.put(Kind.LB.sign, p);

        p++;
        /** .   */ PRIORITY.put(Kind.DOT.value, p);
        /** ... */ PRIORITY.put(Kind.DOT_DOT_DOT.value, p);

        p++;
        // ! + - ++ -- 单目运算符 从右到左
        /** !   */ PRIORITY.put(Kind.BIT_NOT.value, p);
        /** +l  */ PRIORITY.put(Kind.ADD.value + "l", p);
        /** -l  */ PRIORITY.put(Kind.SUB.value + "l", p);
        /** ++  */ PRIORITY.put(Kind.ADD_ADD.value, p);
        /** --  */ PRIORITY.put(Kind.SUB_SUB.value, p);
        // ? 单目运算符 是否元象
        /** ?   */ PRIORITY.put(Kind.ET.value, p);

        p++;
        // * / % 双目运算符 从左到右
        /** *   */ PRIORITY.put(Kind.MUL.value, p);
        /** /   */ PRIORITY.put(Kind.DIV.value, p);
        /** %   */ PRIORITY.put(Kind.MOD.value, p);

        p++;
        // + - 双目运算符 从左到右
        /** +   */ PRIORITY.put(Kind.ADD.value, p);
        /** -   */ PRIORITY.put(Kind.SUB.value, p);

        p++;
        // >> << 位移运算符 双目 从左到右
        /** <<  */ PRIORITY.put(Kind.BIT_LEFT.value, p);
        /** >>  */ PRIORITY.put(Kind.BIT_RIGHT.value, p);
        /** >>> */ PRIORITY.put(Kind.BIT_RIGHT_ZERO.value, p);

        p++;
        // < <= > >= 关系运算符 双目 从左到右
        /** >   */ PRIORITY.put(Kind.GT.value, p);
        /** >=  */ PRIORITY.put(Kind.GE.value, p);
        /** <   */ PRIORITY.put(Kind.LT.value, p);
        /** <=  */ PRIORITY.put(Kind.LE.value, p);

        // ?: 关系运算符 双目 从左到右 是否实例
        /** ?:  */ PRIORITY.put(Kind.IS.value, p);
        // ?< 关系运算符 双目 从左到右 是否属于
        /** ?<  */ PRIORITY.put(Kind.BL.value, p);
        // ?!< 关系运算符 双目 从左到右 是否不属于
        /** ?!< */ PRIORITY.put(Kind.NB.value, p);

        p++;
        // == != 关系运算符 双目 从左到右
        /** ==  */ PRIORITY.put(Kind.EQ.value, p);
        /** !=  */ PRIORITY.put(Kind.NE.value, p);

        p++;
        // & 按位与 双目 从左到右
        /** &   */ PRIORITY.put(Kind.BIT_AND.value, p);

        p++;
        // ^ 按位异或 双目 从左到右
        /** ^   */ PRIORITY.put(Kind.BIT_XOR.value, p);

        p++;
        // | 按位或 双目 从左到右
        /** |   */ PRIORITY.put(Kind.BIT_OR.value, p);

        p++;
        // && 逻辑与 双目 从左到右
        /** &&  */ PRIORITY.put(Kind.LOGICAL_AND.value, p);

        p++;
        // || 逻辑或 双目 从左到右
        /** ||  */ PRIORITY.put(Kind.LOGICAL_OR.value, p);

        p++;
        /** :   */ PRIORITY.put(Kind.COLON.value, p);
        /** :   */ PRIORITY.put(Kind.COLON_BLANK.value, p);

        p++;
        // , 运算 双目 从左到右
        /** ,   */ PRIORITY.put(Kind.COMMA.value, p);

        p++;
        // := = += -= /= %= >>= <<= &= |= 赋值运算 双目 从右到左
        /** :=  */ PRIORITY.put(Kind.ASSIGN_FORCE.value, p);
        /** =   */ PRIORITY.put(Kind.ASSIGN.value, p);
        /** +=  */ PRIORITY.put(Kind.ASSIGN_ADD.value, p);
        /** -=  */ PRIORITY.put(Kind.ASSIGN_SUB.value, p);
        /** *=  */ PRIORITY.put(Kind.ASSIGN_MUL.value, p);
        /** /=  */ PRIORITY.put(Kind.ASSIGN_DIV.value, p);
        /** %=  */ PRIORITY.put(Kind.ASSIGN_MOD.value, p);
        /** <<= */ PRIORITY.put(Kind.ASSIGN_BIT_LEFT.value, p);
        /** >>= */ PRIORITY.put(Kind.ASSIGN_BIT_RIGHT.value, p);
        /** >>>= */ PRIORITY.put(Kind.ASSIGN_BIT_RIGHT_ZERO.value, p);
        /** &=  */ PRIORITY.put(Kind.ASSIGN_BIT_AND.value, p);
        /** ^=  */ PRIORITY.put(Kind.ASSIGN_BIT_XOR.value, p);
        /** |=  */ PRIORITY.put(Kind.ASSIGN_BIT_OR.value, p);

        p++;
        /** ->  */ PRIORITY.put(Kind.LAMBDA.value, p);

        p++;
        /** ;   */ PRIORITY.put(Kind.SEMI.value, p);

        p++;
        // <- 运算 双目 从左到右
        /** <-  */ PRIORITY.put(Kind.IN.value, p);
    }

    protected boolean priority(Token self, Token other, String leftOtherSign) {
        Integer p1;
        if (null == leftOtherSign) {
            p1 = PRIORITY.get(self.kind.value);
        } else {
            p1 = PRIORITY.get(self.kind.value + leftOtherSign);
        }
        if (null == p1) {
            panic("not a operator sign.", self);
            return false;
        }
        if (null == other.kind.value) { return true; }
        Integer p2 = PRIORITY.get(other.kind.value);
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

    protected Token getLastToken(boolean panic) {
        if (channel.isEmpty()) {
            if (panic) {
                panic("channel is empty");
            }
            return null;
        }
        Object o = getLast();
        if (o instanceof Token) {
            return (Token) o;
        } else if (panic) {
            panic("last one is not a token." + o);
        }
        return null;
    }

    protected Token getLastToken() {
        return getLastToken(true);
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

    protected boolean channelEmpty() {
        return channel.isEmpty();
    }

    protected boolean channelFull() {
        return !channel.isEmpty();
    }

    // ================ 工具方法 ================

//    protected static boolean ends(String status, String... ends) {
//        for (String end : ends) {
//            if (status.endsWith(end)) {
//                return true;
//            }
//        }
//        return false;
//    }

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

    protected void panic(String tip) {
        PanicUtil.panic(tip, stream.current(), stream.getOriginTokens().getSourceFile().getName());
    }

    protected void panic(String tip, Token token) {
        PanicUtil.panic(tip, token, stream.getOriginTokens().getSourceFile().getName());
    }

    protected void panic(Token token) {
        panic(token.kind.name() + " is refused here.", token);
    }

    // ================ stream 方法 ================

    public int position() { return stream.position(); }
    public void move(int position) { stream.move(position); }
    public void mark() { stream.mark(); }
    public void recover() { stream.recover(); }
    public void forget() { stream.forget(); }

    public boolean has() { return stream.has(); }
    public Token next() { return stream.next(); }
    public Token glance() { return stream.glance(); }
    public boolean guess(Kind kind) { return stream.guess(kind); }

    public Token glanceSkipSpacesCR() { return stream.glanceSkipSpacesCR(); }
    public Token glanceSkipSpaces() { return stream.glanceSkipSpaces(); }
    public Token current() { return stream.current(); }

    public Channel dropSpaces() { stream.dropSpaces(); return this; }
    public Channel dropCR() { stream.dropCR(); return this; }
    public Channel dropSemi() { stream.dropSemi(); return this; }
    public Channel dropSpacesCR() { stream.dropSpacesCR(); return this; }
    public Channel dropSpacesSemiCR() { stream.dropSpacesSemiCR(); return this; }
    public Channel drop(Kind kind) { stream.drop(kind); return this; }
    public Channel drop(Kind kind, Kind kind2) { stream.drop(kind, kind2); return this; }

}
