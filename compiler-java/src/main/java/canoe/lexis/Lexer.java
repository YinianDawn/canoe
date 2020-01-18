package canoe.lexis;

import canoe.compiler.SourceFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static canoe.util.Util.panic;

/**
 * @author dawn
 */
public class Lexer {

    private static HashMap<String, Kind> KINDS;

    static {
        KINDS = new HashMap<>(97);
        for (Kind kind : Kind.values()) {
            if (0 < kind.getKey().length()) {
                KINDS.put(kind.getKey(), kind);
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

    private CharReader reader;
    private List<Token> tokens = new LinkedList<>();
    private int line = 1;
    private int index = 1;
    private StringBuilder chars = new StringBuilder();

    private Tokens parse() {
        reader = new CharReader(sourceFile.getFile());
        while (reader.hasNext()) {
            check(reader.nextChar());
        }

        merge();

        return new Tokens(sourceFile.getFileName(), tokens);
    }

    private void merge() {
        List<Token> tokens = new ArrayList<>(this.tokens);
        this.tokens.clear();

        int size = tokens.size();
        Token next;
        for (int i = 0; i < size - 1; i++) {
            Token token = tokens.get(i);
            next = tokens.get(i + 1);
            switch (token.getKind()) {
                case COLON:
                    switch (next.getKind()) {
                        case OPEN: if (merge(Kind.COLON_OPEN, token, next, "")) { i++;} continue;
                        case NATIVE: if (merge(Kind.COLON_NATIVE, token, next, "")) { i++;} continue;
                        case GOTO: if (merge(Kind.COLON_GOTO, token, next, "")) { i++;} continue;
                        case ENUM: if (merge(Kind.COLON_ENUM, token, next, "")) { i++;} continue;
                        case ASSIGN: if (merge(Kind.ASSIGN_FORCE, token, next, "")) { i++;} continue;
                        default:
                    } break;
                case ELSE: if (next.getKind() == Kind.IF) { if (merge(Kind.ELSE_IF, token, next, " ")) { i++;} continue; } break;
                case BIT_AND: if (next.getKind() == Kind.BIT_AND) { if (merge(Kind.LOGICAL_AND, token, next, "")) { i++;} continue; } break;
                case BIT_OR: if (next.getKind() == Kind.BIT_OR) { if (merge(Kind.LOGICAL_OR, token, next, "")) { i++;} continue; } break;
                case BIT_NOT: if (next.getKind() == Kind.EQ) { if (merge(Kind.NE, token, next, "")) { i++;} continue; } break;
                case ASSIGN:
                    if (next.getKind() == Kind.ASSIGN) { if (merge(Kind.EQ, token, next, "")) { i++;} continue; }
                    break;
                case GT:
                    if (next.getKind() == Kind.ASSIGN) { if (merge(Kind.GE, token, next, "")) { i++;} continue; }
                    if (next.getKind() == Kind.GT) { if (merge(Kind.BIT_MOVE_RIGHT, token, next, "")) { i++;} continue; }
                    break;
                case LT:
                    if (next.getKind() == Kind.ASSIGN) { if (merge(Kind.LE, token, next, "")) { i++;} continue; }
                    if (next.getKind() == Kind.LT) { if (merge(Kind.BIT_MOVE_LEFT, token, next, "")) { i++;} continue; }
                    break;
                case DOT:
                    if (next.getKind() == Kind.DOT) {
                        if (merge(Kind.DOT_DOT, token, next, "")) { i++; }
                        continue;
                    } else if (next.getKind() == Kind.NUMBER_DECIMAL) {
                        if (token.next(next)) {
                            Token last = null;
                            if (1 < i) {
                                last = tokens.get(i - 1);
                            }
                            if (null != last && last.getKind() == Kind.NUMBER_DECIMAL && last.next(token)) {
                                this.tokens.remove(this.tokens.size() - 1);
                                this.tokens.add(new Token(Kind.REAL_DECIMAL, last.getValue() + "." + next.getValue(), last.getLine(), last.getIndex(), last.getLength() + 1 + next.getLength()));
                            } else {
                                this.tokens.add(new Token(Kind.REAL_DECIMAL, "." + next.getValue(), token.getLine(), token.getIndex(), 1 + next.getLength()));
                            }
                            i++;
                            continue;
                        }
                    }
                    break;
                case ADD: if (next.getKind() == Kind.ASSIGN) {  if (merge(Kind.ADD_ASSIGN, token, next, "")) { i++;} continue; } break;
                case SUB:
                    if (next.getKind() == Kind.GT) {  if (merge(Kind.LAMBDA, token, next, "")) { i++;}  continue; }
                    if (next.getKind() == Kind.ASSIGN) {  if (merge(Kind.SUB_ASSIGN, token, next, "")) { i++;}  continue; }
                    break;
                case MUL: if (next.getKind() == Kind.ASSIGN) {  if (merge(Kind.MUL_ASSIGN, token, next, "")) { i++;} continue; } break;
                case DIV: if (next.getKind() == Kind.ASSIGN) {  if (merge(Kind.DIV_ASSIGN, token, next, "")) { i++;} continue; } break;
                case MOD: if (next.getKind() == Kind.ASSIGN) {  if (merge(Kind.MOD_ASSIGN, token, next, "")) { i++;} continue; } break;
                case CR:
                    while (next.getKind() == Kind.CR) {
                        i++;
                        if (i == size - 1) { break; }
                        next = tokens.get(i + 1);
                    }
                    break;
                default:
            }
            this.tokens.add(token);
        }

        Token thisLast = this.tokens.get(this.tokens.size() - 1);
        Token last = tokens.get(size - 1);
        if (thisLast != last && !(thisLast.getKind() == Kind.CR && last.getKind() == Kind.CR)) {
            this.tokens.add(tokens.get(size - 1));
        }
        while (0 < this.tokens.size() && this.tokens.get(0).getKind() == Kind.CR) {
            this.tokens = this.tokens.subList(1, this.tokens.size());
        }
    }

    private boolean merge(Kind kind, Token t1, Token t2, String join) {
        if (kind.getKey().equals(t1.getKind().getKey() + join + t2.getKind().getKey()) && t1.getLine() == t2.getLine()) {
            int i1 = t1.getIndex();
            int i2 = t2.getIndex();
            int range = i2 - i1 - t1.getLength();
            if (range == join.length()) {
                tokens.add(new Token(kind, null, t1.getLine(), t1.getIndex(), kind.getKey().length()));
                return true;
            }
        }
        tokens.add(t1);
        tokens.add(t2);
        return false;
    }

    private void check(char c) {
        switch (c) {
            case '\r': if (reader.hasNext() && reader.nextChar(false) == '\n') { reader.nextChar(); }
            case '\n': addToken(); addToken(Kind.CR, null); newLine(); break;

            case ' ':
            case '\t': addToken(); index++; break;


            case '\"': addToken(); index++;
                while (reader.hasNext()) {
                    char next = reader.nextChar(false);
                    if (next == '\"') { reader.nextChar(); break; }
                    if (next == '\r' || next == '\n') { panic("string must on single line."); }
                    chars.append(reader.nextChar());
                }
                addToken(Kind.STRING, chars.toString());
                index += tokens.get(tokens.size() - 1).getLength() + 1;
                break;

            case '/' :
                if (reader.hasNext()) {
                    char next = reader.nextChar(false);
                    if (next == '*') {
                        chars.append(c);
                        chars.append(reader.nextChar());
                        int l = line;
                        int i = index;
                        loop:
                        while (reader.hasNext()) {
                            next = reader.nextChar(false);
                            switch (next) {
                                case '*':
                                    chars.append(reader.nextChar());
                                    if (reader.hasNext()) {
                                        if (reader.nextChar(false) == '/') {
                                            chars.append(reader.nextChar());
                                            addToken(Kind.COMMENT_BLOCK);
                                            index = i + (l == line ? 5 : 2);
                                            line = l;
                                            break loop;
                                        }
                                    }
                                    continue;
                                case '\r':
                                    chars.append(reader.nextChar());
                                    if (reader.hasNext()) {
                                        char n = reader.nextChar(false);
                                        if (n == '\n') {
                                            chars.append(reader.nextChar());
                                        }
                                    } l++; i = 1; continue;
                                case '\n': chars.append(reader.nextChar()); l++; i = 1; continue;
                                default:
                            }
                            chars.append(reader.nextChar());
                            i++;
                        }
                        if (0 < chars.length()) {
                            panic("chars can not remain: " + chars.toString());
                        }
                        break;
                    } else if (next == '/') {
                        chars.append(c);
                        while (reader.hasNext()) {
                            char n = reader.nextChar(false);
                            if (n == '\r' || n == '\n') {
                                break;
                            } else {
                                chars.append(reader.nextChar());
                            }
                        }
                        addToken(Kind.COMMENT_LINE);
                        break;
                    }
                }
                addToken(Kind.DIV);index++;break;

            case ':': addToken(); addToken(Kind.COLON); index++; break;

            case '&': addToken(); addToken(Kind.BIT_AND); index++; break;
            case '|': addToken(); addToken(Kind.BIT_OR); index++; break;
            case '!': addToken(); addToken(Kind.BIT_NOT); index++; break;

            case '=': addToken(); addToken(Kind.ASSIGN); index++; break;
            case '>': addToken(); addToken(Kind.GT); index++; break;
            case '<': addToken(); addToken(Kind.LT); index++; break;
            case '.': addToken(); addToken(Kind.DOT); index++; break;
            case '{': addToken(); addToken(Kind.LB); index++; break;
            case '}': addToken(); addToken(Kind.RB); index++; break;
            case '[': addToken(); addToken(Kind.LS); index++; break;
            case ']': addToken(); addToken(Kind.RS); index++; break;
            case '(': addToken(); addToken(Kind.LR); index++; break;
            case ')': addToken(); addToken(Kind.RR); index++; break;
            case ',': addToken(); addToken(Kind.COMMA); index++; break;
            case ';': addToken(); addToken(Kind.SEMI); index++; break;
            case '@': addToken(); addToken(Kind.AT); index++; break;
            case '#': addToken(); addToken(Kind.POUND); index++; break;
            case '+': addToken(); addToken(Kind.ADD); index++; break;
            case '-': addToken(); addToken(Kind.SUB); index++; break;
            case '*': addToken(); addToken(Kind.MUL); index++; break;

            case '%': addToken(); addToken(Kind.MOD); index++; break;
            case '^': addToken(); addToken(Kind.BIT_XOR); index++; break;

            default: chars.append(c);
        }
    }

    private void addToken() {
        if (0 == chars.length()) { return; }
        String value = chars.toString();
        Kind kind = KINDS.get(value);
        if (null != kind) {
            addToken(kind, value);
            index += value.length();
            return;
        }
        if (match(value, Kind.ID, "[A-Za-z_][A-Za-z0-9_]*")) { return; }

        if (match(value, Kind.NUMBER_HEXADECIMAL, "0(x|X)[0-9a-fA-F_]*")) { return; }
        if (match(value, Kind.NUMBER_DECIMAL, "([1-9][0-9_]*|0)")) { return; }
        if (match(value, Kind.NUMBER_BINARY, "0(b|B)[0-1_]*")) { return; }
        if (match(value, Kind.NUMBER_OCTAL, "0[1-7][0-7_]*")) { return; }

        panic("can not identify word: " + value);
    }

    private boolean match(String value, Kind kind, String regex) {
        if (value.matches(regex)) {
            addToken(kind, value);
            index += value.length();
            return true;
        }
        return false;
    }

    private void addToken(Kind kind) {
        addToken(kind, 0 == chars.length() ? null : chars.toString());
    }

    private void addToken(Kind kind, String value) {
        tokens.add(new Token(kind, value, line, index, null == value ? 1 : value.length()));
        if (0 < chars.length()) {
            chars.delete(0, chars.length());
        }
    }

    private void newLine() {
        line++;
        index = 1;
    }
}
