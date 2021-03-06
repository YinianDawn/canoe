package canoe.lexer;

import canoe.compiler.SourceFile;

import java.io.File;
import java.util.*;

import static canoe.lexer.KindSet.BASIC_TYPES;
import static canoe.lexer.KindSet.getKinds;
import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class Lexer {

    private static HashMap<String, Kind> WORDS = new HashMap<>(277);
    static {
        for (Kind kind : getKinds(KindType.KEY_WORD)) {
            if (null != kind.value || kind != Kind.ELSE_IF) {
                if (WORDS.containsKey(kind.value)) {
                    panic("Kind should not repeat. " + kind);
                }
                WORDS.put(kind.value, kind);
            }
        }
    }

    private static HashMap<Kind, HashMap<Kind, Kind>> TWO = new HashMap<>();
    private static HashMap<Kind, HashMap<Kind, HashMap<Kind, Kind>>> THREE = new HashMap<>();
    private static HashMap<Kind, HashMap<Kind, HashMap<Kind, HashMap<Kind, Kind>>>> FOUR = new HashMap<>();
    static {
        HashMap<Character, Kind> single = new HashMap<>(256);
        for (Kind kind : Kind.values()) {
            if (0 == kind.types.length || kind.types[0] == KindType.KEY_WORD) { continue; }
            if (null != kind.value && 1 == kind.value.length()) {
                single.put(kind.value.charAt(0), kind);
            }
        }
        single.put(' ', Kind.BLANK);
        Kind kind1, kind2, kind3, kind4;
        for (Kind kind : Kind.values()) {
            if (0 == kind.types.length
                    || kind.types[0] == KindType.KEY_WORD
                    || kind.types[0] == KindType.CONSTANT
                    || kind.types[0] == KindType.VARIANT
                    || kind.types[0] == KindType.COMMENT
                    || kind.types[0] == KindType.MARK
                    || kind == Kind.DOT_DOT_DOT) { continue; }
            if (null != kind.value && 1 < kind.value.length()) {
                int size = kind.value.length();
                kind1 = single.get(kind.value.charAt(0));
                kind2 = single.get(kind.value.charAt(1));
                switch (size) {
                    case 2:
                        if (null != kind1 && null != kind2) {
                            HashMap<Kind, Kind> map2 = TWO.computeIfAbsent(kind1, k ->new HashMap<>());
                            map2.put(kind2, kind);
                            continue;
                        }
                        break;
                    case 3:
                        kind3 = single.get(kind.value.charAt(2));
                        if (null != kind1 && null != kind2 && null != kind3) {
                            HashMap<Kind, HashMap<Kind, Kind>> map3 = THREE.computeIfAbsent(kind1, k ->new HashMap<>());
                            HashMap<Kind, Kind> map2 = map3.computeIfAbsent(kind2, k -> new HashMap<>());
                            map2.put(kind3, kind);
                            continue;
                        }
                        break;
                    case 4:
                        kind3 = single.get(kind.value.charAt(2));
                        kind4 = single.get(kind.value.charAt(3));
                        if (null != kind1 && null != kind2 && null != kind3 && null != kind4) {
                            HashMap<Kind, HashMap<Kind, HashMap<Kind, Kind>>> map4 = FOUR.computeIfAbsent(kind1, k ->new HashMap<>());
                            HashMap<Kind, HashMap<Kind, Kind>> map3 = map4.computeIfAbsent(kind2, k -> new HashMap<>());
                            HashMap<Kind, Kind> map2 = map3.computeIfAbsent(kind3, k -> new HashMap<>());
                            map2.put(kind4, kind);
                            continue;
                        }
                        break;
                    default:
                }
                panic("what a kind ?! " + kind);
            }
        }
    }


    private static final Token EOF = new Token(Kind.EOF,  1, 1, 0, null);

    private SourceFile sourceFile;
    private CharStream stream;
    private List<Token> tokens;
    private int line = 1;
    private int position = 1;
    private StringBuilder chars ;

    private Lexer(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    private Tokens parse() {
        stream = new CharStream(new File(sourceFile.getName()));
        tokens = new LinkedList<>();
        chars = new StringBuilder();
        while (stream.has()) { eat(stream.next()); }
        merge();
        return new Tokens(sourceFile, tokens);
    }

    private void merge() {
        List<Token> origin = new ArrayList<>(tokens);
        origin.add(EOF); origin.add(EOF); origin.add(EOF);
        tokens.clear();

        int size = origin.size() - 3;
        Token next1, next2, next3, next4;
        HashMap<Kind, HashMap<Kind, HashMap<Kind, Kind>>> map4;
        HashMap<Kind, HashMap<Kind, Kind>> map3;
        HashMap<Kind, Kind> map2;
        for (int i = 0; i < size; i++) {
            next1 = origin.get(i); next2 = origin.get(i + 1);
            next3 = origin.get(i + 2); next4 = origin.get(i + 3);
            switch (next1.kind) {
                case ELSE:
                    if (next2.is(Kind.BLANK) && next3.is(Kind.IF)) {
                        tokens.add(new Token(Kind.ELSE_IF, next1.line, next1.position, 7, null));
                        i++; i++; continue;
                    } break;
                case COLON:
                    if (next2.is(Kind.BLANK, Kind.TAB)) {
                        tokens.add(new Token(Kind.COLON_BLANK, next1.line, next1.position, 2, null));
                        i++; continue;
                    }
                    if (next2.is(Kind.CR)) {
                        tokens.add(new Token(Kind.COLON_BLANK, next1.line, next1.position, 2, null));
                        continue;
                    }
                    break;
                case NUMBER_DEC:
                    if (next2.is(Kind.DOT)) {
                        if (next3.is(Kind.NUMBER_DEC)) {
                            tokens.add(new Token(Kind.DECIMAL, next1.line, next1.position, next1.size + 1 + next3.size, next1.value() + "." + next3.value()));
                            i++; i++; continue;
                        }
                        if (next3.not(Kind.ID, Kind.LR)) {
                            // 1. 后面是id或( 那有可能是 方法调用或强制转换，不看成小数
                            // 还有种可能 1.a() 但是.后换行了，合法，但是这里直接看做小数，不当成换行方法
                            tokens.add(new Token(Kind.DECIMAL, next1.line, next1.position, next1.size + 1, next1.value() + "."));
                            i++; continue;
                        }
                    } break;
                case DOT:
                    if (next2.is(Kind.NUMBER_DEC)) {
                        tokens.add(new Token(Kind.DECIMAL, next1.line, next1.position, 1 + next2.size, "." + next2.value()));
                        i++; continue;
                    } break;
                case BLANK:
                    if (next2.is(Kind.COLON) && next3.is(Kind.BLANK)) {
                        tokens.add(new Token(Kind.BLANK_COLON_BLANK, next1.line, next1.position, 3, null));
                        i += 2; continue;
                    }
                case TAB:
                    if (next2.not(Kind.BLANK, Kind.TAB)) {
                        next1 = new Token(Kind.SPACES, next1.line, next1.position, next1.size, null);
                    } else {
                        // 合并多个空格
                        while (next2.is(Kind.BLANK, Kind.TAB)) {
                            next1 = new Token(Kind.SPACES, next1.line, next1.position, next1.size + next2.size, null);
                            i++;
                            if (size <= i) { break; }
                            next2 = origin.get(i + 1);
                        }
                    }
                    tokens.add(next1); continue;
                case ID:
                    if ("_".equals(next1.value())) {
                        tokens.add(new Token(Kind.UL, next1.line, next1.position, 1, null));
                        continue;
                    }
                default:
            }
            if (next2.is(Kind.DOT) && next3.is(Kind.DOT) && next4.is(Kind.DOT)) {
                if (next1.is(Kind.ID) || BASIC_TYPES.contains(next1.kind)) {
                    tokens.add(next1);
                    tokens.add(new Token(Kind.DOT_DOT_DOT, next2.line, next2.position, 3, null));
                    i += 3; continue;
                }
            }

            map4 = FOUR.get(next1.kind);
            if (null != map4) {
                map3 = map4.get(next2.kind);
                if (null != map3) {
                    map2 = map3.get(next3.kind);
                    if (null != map2) {
                        Kind kind = map2.get(next4.kind);
                        if (null != kind && next(next1, next2, next3, next4)) {
                            tokens.add(new Token(kind, next1.line, next1.position, kind.value.length(), null));
                            i += 3; continue;
                        }
                    }
                }
            }
            map3 = THREE.get(next1.kind);
            if (null != map3) {
                map2 = map3.get(next2.kind);
                if (null != map2) {
                    Kind kind = map2.get(next3.kind);
                    if (null != kind && next(next1, next2, next3)) {
                        tokens.add(new Token(kind, next1.line, next1.position, kind.value.length(), null));
                        i += 2; continue;
                    }
                }
            }
            map2 = TWO.get(next1.kind);
            if (null != map2) {
                Kind kind = map2.get(next2.kind);
                if (null != kind && next(next1, next2)) {
                    tokens.add(new Token(kind, next1.line, next1.position, kind.value.length(), null));
                    i += 1; continue;
                }
            }
            tokens.add(next1);
        }

        while (EOF == tokens.get(tokens.size() - 1)) { tokens.remove(tokens.size() - 1); }
        Token last = tokens.get(tokens.size() - 1);
        if (!last.isCR()) {
            last = new Token(Kind.CR, last.line, last.position + last.size, 0, null);
            tokens.add(last);
        }
        tokens.add(new Token(Kind.EOF, last.line, last.position + last.size, 0, null));
    }

    private static boolean next(Token... tokens) {
        if (tokens.length <= 1) { return true; }
        for (int i = 0; i < tokens.length - 1; i++) {
            if (!tokens[i].next(tokens[i + 1])) {
                return false;
            }
        }
        return true;
    }

    private void eat(char c) {
        switch (c) {
            case '\r': if (stream.guess('\n')) { stream.next(); }
            case '\n': addToken(); addToken(Kind.CR, null); newLine(); break;

            case '\t': addToken(); addSpaceToken(Kind.TAB);    position++; break;
            case ' ':  addToken(); addSpaceToken(Kind.BLANK);  position++; break;

            case '\"': addToken(); addStringToken(); break;
            case '\'': addToken(); addCharToken(); break;

            case '/' : addToken();
                if (stream.guess('*')) { addBlockCommentToken(); break; }
                if (stream.guess('/')) { addLineCommentToken(); break; }
                addToken(Kind.DIV); position++; break;

            case '=': addToken(); addToken(Kind.ASSIGN); position++; break;
            case '>': addToken(); addToken(Kind.GT); position++; break;
            case '<': addToken(); addToken(Kind.LT); position++; break;

            case '?': addToken(); addToken(Kind.ET); position++; break;
            case ':': addToken(); addToken(Kind.COLON); position++; break;

            case '!': addToken(); addToken(Kind.LOGIC_NOT); position++; break;

            case '&': addToken(); addToken(Kind.BIT_AND); position++; break;
            case '|': addToken(); addToken(Kind.BIT_OR);  position++; break;
            case '~': addToken(); addToken(Kind.BIT_NOT);  position++; break;

            case '+': addToken(); addToken(Kind.ADD); position++; break;
            case '-': addToken(); addToken(Kind.SUB); position++; break;
            case '*': addToken(); addToken(Kind.MUL); position++; break;
            case '%': addToken(); addToken(Kind.MOD); position++; break;
            case '^': addToken(); addToken(Kind.EXP); position++; break;

            case '.': addToken(); addToken(Kind.DOT); position++; break;

            case ',': addToken(); addToken(Kind.COMMA); position++; break;
            case ';': addToken(); addToken(Kind.SEMI);  position++; break;

            case '{': addToken(); addToken(Kind.LB); position++; break;
            case '}': addToken(); addToken(Kind.RB); position++; break;
            case '[': addToken(); addToken(Kind.LS); position++; break;
            case ']': addToken(); addToken(Kind.RS); position++; break;
            case '(': addToken(); addToken(Kind.LR); position++; break;
            case ')': addToken(); addToken(Kind.RR); position++; break;

            case '@': addToken(); addToken(Kind.AT);    position++; break;
            case '#': addToken(); addToken(Kind.HASH);  position++; break;
            case '`': addToken(); addToken(Kind.ANTI);  position++; break;
            case '$': addToken(); addToken(Kind.DOLLAR);  position++; break;
            case '\\': addToken(); addToken(Kind.BACK);  position++; break;

            default: chars.append(c);
        }
    }

    private void addToken() {
        if (0 == chars.length()) { return; }
        String value = chars.toString();
        Kind kind = WORDS.get(value);
        if (null != kind) {
            addToken(kind, value);
            position += value.length();
            return;
        }
        if (match(value, Kind.ID, "[A-Za-z_][A-Za-z0-9_]*")) { return; }

        if (match(value, Kind.NUMBER_DEC, "([1-9][0-9_]*|0)")) { return; }
        if (match(value, Kind.NUMBER_HEX, "0(x|X)[0-9a-fA-F_]*")) { return; }
        if (match(value, Kind.NUMBER_BIN, "0(b|B)[0-1_]*")) { return; }
        if (match(value, Kind.NUMBER_OCT, "0[1-7][0-7_]*")) { return; }

        panic("can not identify word: " + value, new Token(Kind.STRING, line, position, value.length(), value), sourceFile.getName());
    }

    private boolean match(String value, Kind kind, String regex) {
        if (value.matches(regex)) {
            addToken(kind, value);
            position += value.length();
            return true;
        }
        return false;
    }

    private void addToken(Kind kind) {
        addToken(kind, 0 == chars.length() ? null : chars.toString());
    }

    private void addToken(Kind kind, String value) {
        int size = null != value ? value.length() : (null != kind.value ? kind.value.length() : 0);
        tokens.add(new Token(kind, line, position, size, value));
        clear();
    }

    private void addSpaceToken(Kind kind) {
        tokens.add(new Token(kind, line, position, 1, null));
        clear();
    }

    private void addStringToken() {
        position++;
        while (stream.has()) {
            char next = stream.glance();
            if (next == '\\') {
                chars.append(stream.next());
                if (stream.has()) {
                    switch (stream.glance()) {
                        case 'a': case 'b': case 'f': case 'n': case 'r': case 't': case 'v':
                        case '\\': case '"': case '\'':
                            chars.append(stream.next());
                            continue;
                        case 'u':
                            chars.append(stream.next());
                            // 后面4个应该是数字
                            String n = stream.next4();
                            if (n.matches("[0-9A-Fa-f]{4}")) {
                                chars.append(n);
                                continue;
                            }
                            chars.deleteCharAt(chars.length() - 1);
                        default:
                    }
                }
                panic("wrong \\ usage", new Token(Kind.STRING, line, position + chars.length() - 1, 1, "\\"), sourceFile.getName());                continue;
            }
            if (next == '\"') { stream.next(); break; }
            if (next == '\r' || next == '\n') { panic("string must on single line.", new Token(Kind.STRING, line, position, chars.length(), chars.toString()), sourceFile.getName()); }
            chars.append(stream.next());
        }
        addToken(Kind.STRING, chars.toString());
        position += tokens.get(tokens.size() - 1).size + 1;
    }

    private void addCharToken() {
        position++;
        while (stream.has()) {
            char next = stream.glance();
            if (next == '\\') { chars.append(stream.next()); if (stream.has()) {chars.append(stream.next());} continue; }
            if (next == '\'') { stream.next(); break; }
            chars.append(stream.next());
        }
        String value = chars.toString();
        switch (value.length()) {
            case 0: panic("char must has something", new Token(Kind.CHAR, line, position, value.length(), value), sourceFile.getName()); break;
            case 1:
                switch (value.charAt(0)) {
                    case '\\':
                    case '\'':
                        panic(value + " can not be alone", new Token(Kind.CHAR, line, position, value.length(), value), sourceFile.getName());
                    default:
                }
                break;
            default:
                if (value.charAt(0) != '\\') {
                    panic("\\ must be first", new Token(Kind.CHAR, line, position, value.length(), value), sourceFile.getName());
                }
                switch (value.charAt(1)) {
                    case 'a': case 'b': case 'f': case 'n': case 'r': case 't': case 'v':
                    case '\\': case '"': case '\'':
                        if (2 != value.length()) {
                            panic("wrong char", new Token(Kind.CHAR, line, position, value.length(), value), sourceFile.getName());
                        }
                        break;
                    case 'u':
                        if (6 != value.length()) {
                            panic("wrong char", new Token(Kind.CHAR, line, position, value.length(), value), sourceFile.getName());
                        }
                        if (!value.substring(2).matches("[0-9A-Fa-f]{4}")) {
                            panic("wrong char", new Token(Kind.CHAR, line, position, value.length(), value), sourceFile.getName());
                        }
                        break;
                    default:
                        panic("wrong char", new Token(Kind.CHAR, line, position, value.length(), value), sourceFile.getName());
                }
        }
        addToken(Kind.CHAR, value);
        position += tokens.get(tokens.size() - 1).size + 1;
    }

    private void addBlockCommentToken() {
        chars.append('/');
        chars.append(stream.next());
        char next;
        int count = 1;
        int blockLine = line;
        int blockPosition = position;
        loop:
        while (stream.has()) {
            next = stream.glance();
            switch (next) {
                case '/':
                    chars.append(stream.next());
                    if (stream.guess('*')) {
                        chars.append(stream.next());
                        count++;
                    }
                    continue;
                case '*':
                    chars.append(stream.next());
                    if (stream.guess('/')) {
                        // 结束块注释
                        chars.append(stream.next());
                        count--;
                        if (count <= 0) {
                            addToken(Kind.COMMENT_BLOCK);
                            position = blockLine == line ? position: blockPosition + 2;
                            line = blockLine;
                            break loop;
                        }
                    }
                    continue;
                case '\r':
                    chars.append(stream.next());
                    if (stream.guess('\n')) { chars.append(stream.next()); }
                    blockLine++; blockPosition = 1; continue;
                case '\n':
                    chars.append(stream.next());
                    blockLine++; blockPosition = 1; continue;
                default:
            }
            chars.append(stream.next());
            blockPosition++;
        }
        if (0 < chars.length()) {
            panic("chars can not remain: " + chars.toString(), tokens.get(tokens.size() - 1), sourceFile.getName());
        }
    }

    private void addLineCommentToken() {
        chars.append('/');
        chars.append(stream.next());
        while (stream.has()) {
            if (stream.guess('\r', '\n')) { break; }
            chars.append(stream.next());
        }
        addToken(Kind.COMMENT_LINE);
    }

    private void clear() { if (0 < chars.length()) { chars.delete(0, chars.length()); } }

    private void newLine() { line++; position = 1; }

    public static Tokens parseTokens(SourceFile sourceFile) {
        return new Lexer(sourceFile).parse();
    }

}
