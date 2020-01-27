package canoe.lexer;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class Token {

    public final Kind kind;
    public final int line;
    public final int position;
    public final int size;

    private final String value;

    public Token(Kind kind, int line, int position, int size, String value) {
        this.kind = kind;
        if (null == kind) { panic("is can not be null."); }
        this.line = line;
        if (line < 1) { panic("line number can not less then 1."); }
        this.position = position;
        if (position < 1) { panic("position index can not less then 1."); }
        this.size = size;
        if (size < 0) { panic("size of " + kind.name() + " can not less then 0."); }
        switch (kind) {
            case BLANK: case SPACES: case CR: case EOF: break;
            default: if (null != kind.value && size != kind.value.length()) {
                panic("size of " + kind.name() + " should be " + kind.value.length() + ".");
            }
        }
        this.value = value;
        if (null == value && null == kind.value) {
            panic("value of " + kind.name() + " can not be null.");
        }
    }

    public String value() {
        return null == value ? kind.value : value;
    }

    @Override
    public String toString() {
        return String.format("%s [%d:%d:%d] %s", kind.name(), line, position, size, value());
    }

    public boolean next(Token next) { return line == next.line && position + size == next.position; }
    public boolean line(Token next) { return line == next.line; }

    public boolean isComment() { return kind == Kind.COMMENT_LINE || kind == Kind.COMMENT_BLOCK; }
    public boolean isSpaces() { return kind == Kind.SPACES; }
    public boolean isCR() { return kind == Kind.CR; }
    public boolean isSemi() { return kind == Kind.SEMI; }
    public boolean isSpacesCR() { return kind == Kind.SPACES || kind == Kind.CR; }
    public boolean isSpacesSemiCR() { return kind == Kind.SPACES || kind == Kind.CR || kind == Kind.SEMI; }
//    public boolean isDot() { return kind == Kind.DOT; }
//    public boolean isColon() { return kind == Kind.COLON; }

    public boolean is(Kind other) { return kind == other; }
    public boolean is(Kind kind, Kind kind2) { return is(kind) || is(kind2); }
//    public boolean is(Kind kind, Kind kind2, Kind kind3) { return is(kind) || is(kind2) || is(kind3); }
    public boolean is(Iterable<Kind> kinds) { for (Kind kind : kinds) { if (is(kind)) { return true; } } return false; }

    public boolean not(Kind other) { return kind != other; }
    public boolean not(Kind kind, Kind kind2) { return not(kind) && not(kind2); }
    public boolean not(Kind kind, Kind kind2, Kind kind3) { return not(kind) && not(kind2) && not(kind3); }
//    public boolean not(Kind[] kinds) { for (Kind kind : kinds) { if (is(kind)) { return false; } } return true; }
    public boolean not(Iterable<Kind> kinds) { for (Kind kind : kinds) { if (is(kind)) { return false; } } return true; }

}
