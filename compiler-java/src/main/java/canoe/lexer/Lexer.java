package canoe.lexer;

import canoe.compiler.SourceFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class Lexer {

    public static HashMap<String, Kind> KINDS;

    static {
        KINDS = new HashMap<>(277);
        for (Kind kind : Kind.values()) {
            if (null != kind.sign) {
                if (KINDS.containsKey(kind.sign)) {
                    panic("Kind should not repeat. " + kind);
                }
                KINDS.put(kind.sign, kind);
            }
        }
    }

    public static Tokens parseTokens(SourceFile sourceFile) {
        return new Lexer(sourceFile).parse();
    }

    private SourceFile sourceFile;

    private Lexer(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    private CharStream stream;
    private List<Token> tokens;
    private int line = 1;
    private int position = 1;
    private StringBuilder chars ;

    private static final Token EOF = new Token(Kind.EOF, null, 1, 1, 0);
    private Token next;
    private Token next2;

    private Tokens parse() {
        stream = new CharStream(new File(sourceFile.getName()));
        tokens = new LinkedList<>();
        chars = new StringBuilder();
        while (stream.has()) { eat(stream.next()); }
        merge();
        return new Tokens(sourceFile, tokens);
    }

    private void merge() {
        List<Token> origin = new ArrayList<>(this.tokens);
        origin.add(EOF);
        origin.add(EOF);

        this.tokens.clear();
        int size = origin.size() - 2;
        Token next3;
        for (int i = 0; i < size; i++) {
            next = origin.get(i);
            next2 = origin.get(i + 1);
            next3 = origin.get(i + 2);
            switch (next.kind) {
                case ELSE:
                    if (next2.is(Kind.BLANK) && next2.size == 1 && next3.is(Kind.IF)) {
                        tokens.add(new Token(Kind.ELSE_IF, null, next.line, next.position, 7));
                        i++; i++; continue;
                    } break;
                case BIT_AND:
                    if (next2.is(Kind.BIT_AND) && merge(Kind.LOGICAL_AND)) { i++; continue; }
                    if (next2.is(Kind.ASSIGN) && merge(Kind.BIT_AND_ASSIGN)) { i++; continue; }
                    break;
                case BIT_OR:
                    if (next2.is(Kind.BIT_OR) && merge(Kind.LOGICAL_OR)) { i++; continue; }
                    if (next2.is(Kind.ASSIGN) && merge(Kind.BIT_OR_ASSIGN)) { i++; continue; }
                    break;
                case BIT_XOR: if (next2.is(Kind.ASSIGN) && merge(Kind.BIT_XOR_ASSIGN)) { i++; continue; } break;
                case BIT_NOT: if (next2.is(Kind.ASSIGN) && merge(Kind.NE)) { i++; continue; } break;
                case ASSIGN:
                    if (next2.is(Kind.ASSIGN) && merge(Kind.EQ)) { i++; continue; }
                    break;
                case COLON:
                    if (next2.is(Kind.ASSIGN) && merge(Kind.ASSIGN_FORCE)) { i++; continue; }
                    if (next2.is(Kind.BLANK) && next2.size <= 2) {
                        tokens.add(new Token(Kind.COLON_BLANK, null, next.line, next.position, 2));
                        i++; continue;
                    }
                    if (next2.is(Kind.CR)) {
                        tokens.add(new Token(Kind.COLON_BLANK, null, next.line, next.position, 2));
                        continue;
                    }
                    break;
                case GT:
                    if (next2.is(Kind.ASSIGN) && merge(Kind.GE)) { i++; continue; }
                    if (next2.is(Kind.GT)) {
                        if (next3.is(Kind.ASSIGN) && next.next(next2) && next2.next(next3)) {
                            this.tokens.add(new Token(Kind.BIT_RIGHT_ASSIGN, null, next.line, next.position, 3));
                            i++; i++; continue;
                        } else if (merge(Kind.BIT_RIGHT)) { i++; continue; }
                    } break;
                case LT:
                    if (next2.is(Kind.ASSIGN) && merge(Kind.LE)) { i++; continue; }
                    if (next2.is(Kind.SUB) && merge(Kind.IS)) { i++; continue; }
                    if (next2.is(Kind.LT)) {
                        if (next3.is(Kind.ASSIGN) && next.next(next2) && next2.next(next3)) {
                            this.tokens.add(new Token(Kind.BIT_LEFT_ASSIGN, null, next.line, next.position, 3));
                            i++; i++; continue;
                        } else if (merge(Kind.BIT_LEFT)) { i++; continue; }
                    } break;
                case DOT:
                    if (next2.is(Kind.DOT)) {
                        if (next3.is(Kind.DOT) && next.next(next2) && next2.next(next3)) {
                            this.tokens.add(new Token(Kind.DOT_DOT_DOT, null, next.line, next.position, 3));
                            i++; i++; continue;
                        } else if (merge(Kind.DOT_DOT)) { i++; continue; }
                    } else {
                        Token last = null;
                        if (1 < i) { last = origin.get(i - 1); }
                        if (next2.is(Kind.NUMBER_DEC) && next.next(next2)) {
                            if (null != last && last.is(Kind.NUMBER_DEC) && last.next(next)) {
                                this.tokens.remove(this.tokens.size() - 1);
                                this.tokens.add(new Token(Kind.DECIMAL, last.getValue() + "." + next2.getValue(), last.line, last.position, last.size + 1 + next2.size));
                            } else {
                                this.tokens.add(new Token(Kind.DECIMAL, "." + next2.getValue(), next.line, next.position, 1 + next2.size));
                            }
                            i++; continue;
                        } else if (null != last && last.is(Kind.NUMBER_DEC) && last.next(next)) {
                            this.tokens.remove(this.tokens.size() - 1);
                            this.tokens.add(new Token(Kind.DECIMAL, last.getValue() + ".", last.line, last.position, last.size + 1));
                            continue;
                        }
                    } break;
                case ADD:
                    if (next2.is(Kind.ADD) && merge(Kind.ADD_ADD)) { i++; continue; }
                    if (next2.is(Kind.ASSIGN) && merge(Kind.ADD_ASSIGN)) { i++; continue; }
                    break;
                case SUB:
                    if (next2.is(Kind.SUB) && merge(Kind.SUB_SUB)) { i++; continue; }
                    if (next2.is(Kind.ASSIGN) && merge(Kind.SUB_ASSIGN)) { i++; continue; }
                    if (next2.is(Kind.GT) && merge(Kind.LAMBDA)) { i++; continue; }
                    break;
                case MUL: if (next2.is(Kind.ASSIGN) && merge(Kind.MUL_ASSIGN)) { i++; continue; } break;
                case DIV: if (next2.is(Kind.ASSIGN) && merge(Kind.DIV_ASSIGN)) { i++; continue; } break;
                case MOD: if (next2.is(Kind.ASSIGN) && merge(Kind.MOD_ASSIGN)) { i++; continue; } break;
                case QUESTION: if (next2.is(Kind.COLON) && merge(Kind.IS)) { i++; continue; } break;
                case BLANK:
                    if (next2.not(Kind.BLANK)) {
                        next = new Token(Kind.SPACES, null, next.line, next.position, next.size);
                        break;
                    }
                    // 合并多个空格
                    while (next2.is(Kind.BLANK)) {
                        i++;
                        next = new Token(Kind.SPACES, null, next.line, next.position, next.size + next2.size);
                        if (size <= i) { break; }
                        next2 = origin.get(i + 1);
                    }
                    break;
                default:
            }
            this.tokens.add(next);
        }
        while (EOF == this.tokens.get(this.tokens.size() - 1)) { this.tokens.remove(this.tokens.size() - 1); }
        Token last = this.tokens.get(this.tokens.size() - 1);
        if (!last.isCR()) {
            last = new Token(Kind.CR, null, last.line, last.position + last.size, 0);
            this.tokens.add(last);
        }
        this.tokens.add(new Token(Kind.EOF, null, last.line, last.position + last.size, 0));
    }

    private boolean merge(Kind kind) {
        if (kind.sign.equals(next.kind.sign + next2.kind.sign) && next.next(next2)) {
            tokens.add(new Token(kind, null, next.line, next.position, kind.sign.length()));
            return true;
        }
        return false;
    }

    private void eat(char c) {
        switch (c) {
            case '\r': if (stream.has() && stream.next('\n')) { stream.next(); }
            case '\n': addToken(); addToken(Kind.CR, null); newLine(); break;

            case '\t': addToken(); addBlankToken(2); position++; break;
            case ' ':  addToken(); addBlankToken(1); position++; break;

            case '\"': addToken(); position++;
                while (stream.has()) {
                    char next = stream.next(false);
                    if (next == '\"') { stream.next(); break; }
                    if (next == '\r' || next == '\n') { panic("string must on single line.", sourceFile.getName(), new Token(Kind.STRING, chars.toString(), line, position, chars.length())); }
                    chars.append(stream.next());
                }
                addToken(Kind.STRING, chars.toString());
                position += tokens.get(tokens.size() - 1).size + 1;
                break;

            case '/' :
                if (stream.has()) {
                    char next = stream.next(false);
                    if (next == '*') {
                        // 是块注释
                        chars.append(c);
                        chars.append(stream.next());
                        int blockLine = line;
                        int blockPosition = position;
                        loop:
                        while (stream.has()) {
                            next = stream.next(false);
                            switch (next) {
                                case '*':
                                    chars.append(stream.next());
                                    if (stream.next('/')) {
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
                                    if (stream.next('\n')) { chars.append(stream.next()); }
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
                            char n = stream.next(false);
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

            case ':': addToken(); addToken(Kind.COLON); position++; break;

            case '&': addToken(); addToken(Kind.BIT_AND); position++; break;
            case '|': addToken(); addToken(Kind.BIT_OR); position++; break;
            case '!': addToken(); addToken(Kind.BIT_NOT); position++; break;

            case '=': addToken(); addToken(Kind.ASSIGN); position++; break;
            case '>': addToken(); addToken(Kind.GT); position++; break;
            case '<': addToken(); addToken(Kind.LT); position++; break;

            case '.': addToken(); addToken(Kind.DOT); position++; break;

            case '{': addToken(); addToken(Kind.LB); position++; break;
            case '}': addToken(); addToken(Kind.RB); position++; break;
            case '[': addToken(); addToken(Kind.LS); position++; break;
            case ']': addToken(); addToken(Kind.RS); position++; break;
            case '(': addToken(); addToken(Kind.LR); position++; break;
            case ')': addToken(); addToken(Kind.RR); position++; break;

            case ',': addToken(); addToken(Kind.COMMA); position++; break;
            case ';': addToken(); addToken(Kind.SEMI); position++; break;
            case '@': addToken(); addToken(Kind.AT); position++; break;
            case '#': addToken(); addToken(Kind.HASH); position++; break;

            case '+': addToken(); addToken(Kind.ADD); position++; break;
            case '-': addToken(); addToken(Kind.SUB); position++; break;
            case '*': addToken(); addToken(Kind.MUL); position++; break;

            case '%': addToken(); addToken(Kind.MOD); position++; break;
            case '^': addToken(); addToken(Kind.BIT_XOR); position++; break;

            default: chars.append(c);
        }
    }

    private void addToken() {
        if (0 == chars.length()) { return; }
        String value = chars.toString();
        Kind kind = KINDS.get(value);
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

        panic("can not identify word: " + value, sourceFile.getName(), new Token(Kind.STRING, value, line, position, value.length()));
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
        int size = null == value ? 0 : value.length();
        if (0 == size) {
            switch (kind) {
                case CR: case SPACES: case EOF: break;
                default: size = kind.sign.length();
            }
        }
        tokens.add(new Token(kind, value, line, position, size));
        clear();
    }

    private void addBlankToken(int size) {
        Token token = new Token(Kind.BLANK, null, line, position, size);
        tokens.add(token);
        clear();
    }

    private void clear() { if (0 < chars.length()) { chars.delete(0, chars.length()); } }

    private void newLine() { line++; position = 1; }

}
