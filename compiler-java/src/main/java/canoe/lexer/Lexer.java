package canoe.lexer;

import canoe.compiler.SourceFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static canoe.lexer.KindSet.SINGLE_KEY_WORDS;
import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class Lexer {

    static HashMap<String, Kind> WORDS = new HashMap<>(277);
    static {
        for (Kind kind : SINGLE_KEY_WORDS) {
            if (null != kind.value) {
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
        HashMap<Character, Kind> kinds = new HashMap<>(256);
        for (Kind kind : Kind.values()) {
            if (0 == kind.types.length || kind.types[0] == KindType.KEY_WORD) { continue; }
            if (null != kind.value && 1 == kind.value.length()) {
                kinds.put(kind.value.charAt(0), kind);
            }
        }
        kinds.put(' ', Kind.BLANK);
        Kind kind1, kind2, kind3, kind4;
        for (Kind kind : Kind.values()) {
            if (0 == kind.types.length
                    || kind.types[0] == KindType.KEY_WORD
                    || kind.types[0] == KindType.COMMENT
                    || kind.types[0] == KindType.MARK) { continue; }
            if (null != kind.value && 1 < kind.value.length()) {
                int size = kind.value.length();
                switch (size) {
                    case 2:
                        kind1 = kinds.get(kind.value.charAt(0));
                        kind2 = kinds.get(kind.value.charAt(1));
                        if (null != kind1 && null != kind2) {
                            HashMap<Kind, Kind> map2 = TWO.computeIfAbsent(kind1, k ->new HashMap<>());
                            map2.put(kind2, kind);
                            continue;
                        }
                        break;
                    case 3:
                        kind1 = kinds.get(kind.value.charAt(0));
                        kind2 = kinds.get(kind.value.charAt(1));
                        kind3 = kinds.get(kind.value.charAt(2));
                        if (null != kind1 && null != kind2 && null != kind3) {
                            HashMap<Kind, HashMap<Kind, Kind>> map3 = THREE.computeIfAbsent(kind1, k ->new HashMap<>());
                            HashMap<Kind, Kind> map2 = map3.computeIfAbsent(kind2, k -> new HashMap<>());
                            map2.put(kind3, kind);
                            continue;
                        }
                        break;
                    case 4:
                        kind1 = kinds.get(kind.value.charAt(0));
                        kind2 = kinds.get(kind.value.charAt(1));
                        kind3 = kinds.get(kind.value.charAt(2));
                        kind4 = kinds.get(kind.value.charAt(3));
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
        origin.add(EOF);
        origin.add(EOF);
        origin.add(EOF);
        tokens.clear();

        int size = origin.size() - 3;
        Token next1, next2, next3, next4;
        HashMap<Kind, HashMap<Kind, HashMap<Kind, Kind>>> map4;
        HashMap<Kind, HashMap<Kind, Kind>> map3;
        HashMap<Kind, Kind> map2;
        for (int i = 0; i < size; i++) {
            next1 = origin.get(i);
            next2 = origin.get(i + 1);
            next3 = origin.get(i + 2);
            next4 = origin.get(i + 3);
            switch (next1.kind) {
                case ELSE:
                    if (next2.is(Kind.BLANK) && next3.is(Kind.IF) && next(next1, next2, next3)) {
                        tokens.add(new Token(Kind.ELSE_IF, next1.line, next1.position, 7, null));
                        i++; i++; continue;
                    } break;
                case COLON:
                    if (next2.is(Kind.BLANK, Kind.TAB) && next(next1, next2)) {
                        tokens.add(new Token(Kind.COLON_BLANK, next1.line, next1.position, 2, null));
                        i++; continue;
                    }
                    if (next2.is(Kind.CR) && next(next1, next2)) {
                        tokens.add(new Token(Kind.COLON_BLANK, next1.line, next1.position, 2, null));
                        continue;
                    }
                    break;
                case NUMBER_DEC:
                    if (next2.is(Kind.DOT) && next(next1, next2)) {
                        if (next3.is(Kind.NUMBER_DEC) && next(next2, next3)) {
                            tokens.add(new Token(Kind.DECIMAL, next1.line, next1.position, next1.size + 1 + next3.size, next1.value() + "." + next3.value()));
                            i++; i++; continue;
                        }
                        if (next3.not(Kind.ID, Kind.LR)) {
                            // 1. 后面是id或( 那有可能是 方法调用或强制转换，不看成小数
                            // 还有种可能 1.a() 但是.后换行了，合法，但是这里直接看做小数，不当成换行方法
                            tokens.add(new Token(Kind.DECIMAL, next1.line, next1.position, next1.size + 1, next1.value() + "."));
                            i++; continue;
                        }
                    }
                    break;
                case DOT:
                    if (next2.is(Kind.NUMBER_DEC) && next(next1, next2)) {
                        tokens.add(new Token(Kind.DECIMAL, next1.line, next1.position, 1 + next2.size, "." + next2.value()));
                        i++; continue;
                    }
                    break;
                case BLANK:
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
                default:
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
                            i++; i++; i++; continue;
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
                        i++; i++; continue;
                    }
                }
            }
            map2 = TWO.get(next1.kind);
            if (null != map2) {
                Kind kind = map2.get(next2.kind);
                if (null != kind && next(next1, next2)) {
                    tokens.add(new Token(kind, next1.line, next1.position, kind.value.length(), null));
                    i++; continue;
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

            case '\"': addToken(); position++;
                while (stream.has()) {
                    char next = stream.glance();
                    if (next == '\"') { stream.next(); break; }
                    if (next == '\r' || next == '\n') { panic("string must on single line.", sourceFile.getName(), new Token(Kind.STRING, line, position, chars.length(), chars.toString())); }
                    chars.append(stream.next());
                }
                addToken(Kind.STRING, chars.toString());
                position += tokens.get(tokens.size() - 1).size + 1;
                break;

            case '/' :
                if (stream.has()) {
                    char next = stream.glance();
                    if (next == '*') {
                        // 是块注释
                        chars.append(c);
                        chars.append(stream.next());
                        int blockLine = line;
                        int blockPosition = position;
                        loop:
                        while (stream.has()) {
                            next = stream.glance();
                            switch (next) {
                                case '*':
                                    chars.append(stream.next());
                                    if (stream.guess('/')) {
                                        // 结束块注释
                                        chars.append(stream.next());
                                        addToken(Kind.COMMENT_BLOCK);
                                        position = blockLine == line ? position: blockPosition + 2;
                                        line = blockLine;
                                        break loop;
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
                            panic("chars can not remain: " + chars.toString(), sourceFile.getName(), tokens.get(tokens.size() - 1));
                        }
                        break;
                    } else if (next == '/') {
                        chars.append(c);
                        while (stream.has()) {
                            char n = stream.glance();
                            if (n == '\r' || n == '\n') {
                                break;
                            } else {
                                chars.append(stream.next());
                            }
                        }
                        addToken(Kind.COMMENT_LINE);
                        break;
                    }
                }
                // 除法符号
                addToken(Kind.DIV);
                position++;
                break;

            case '=': addToken(); addToken(Kind.ASSIGN); position++; break;
            case '>': addToken(); addToken(Kind.GT); position++; break;
            case '<': addToken(); addToken(Kind.LT); position++; break;

            case '?': addToken(); addToken(Kind.ET); position++; break;
            case ':': addToken(); addToken(Kind.COLON); position++; break;

            case '&': addToken(); addToken(Kind.BIT_AND); position++; break;
            case '|': addToken(); addToken(Kind.BIT_OR);  position++; break;
            case '!': addToken(); addToken(Kind.BIT_NOT); position++; break;

            case '+': addToken(); addToken(Kind.ADD); position++; break;
            case '-': addToken(); addToken(Kind.SUB); position++; break;
            case '*': addToken(); addToken(Kind.MUL); position++; break;
            case '%': addToken(); addToken(Kind.MOD); position++; break;

            case '^': addToken(); addToken(Kind.BIT_XOR); position++; break;

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
            case '~': addToken(); addToken(Kind.WAVE);  position++; break;
            case '$': addToken(); addToken(Kind.DOLLAR);  position++; break;

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

        panic("can not identify word: " + value, sourceFile.getName(), new Token(Kind.STRING, line, position, value.length(), value));
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

    private void clear() { if (0 < chars.length()) { chars.delete(0, chars.length()); } }

    private void newLine() { line++; position = 1; }

    public static Tokens parseTokens(SourceFile sourceFile) {
        return new Lexer(sourceFile).parse();
    }

}
