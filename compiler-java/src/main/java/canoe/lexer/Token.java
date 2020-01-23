package canoe.lexer;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class Token {

    public final Kind kind;

    private final String value;

    public final int line;

    public final int position;

    public final int size;

    public Token(Kind kind, String value, int line, int position, int size) {
        this.kind = kind;
        if (null == kind) {
            panic("is can not be null.");
        }
        this.value = value;
        if (null == value && null == kind.sign) {
            panic("both value and sign of is are null, can not be.");
        }
        this.line = line;
        if (line < 1) {
            panic("line number can not less then 1.");
        }
        this.position = position;
        if (position < 1) {
            panic("position index can not less then 1.");
        }
        this.size = size;
        if (size < 0) {
            panic("size of " + kind.name() + " can not less then 0.");
        }
        switch (kind) {
            case BLANK: case SPACES: case CR: case EOF: return;
            default:
        }
        if (null != kind.sign && size != kind.sign.length()) {
            panic("size of " + kind.name() + " should be " + kind.sign.length() + ".");
        }
    }

    public String getValue() {
        return null == value ? kind.sign : value;
    }

    @Override
    public String toString() {
        return String.format("%s [%d:%d:%d] %s", kind.name(), line, position, size, getValue());
    }

    public boolean next(Token next) {
        return line(next) && this.position + this.size == next.position;
    }

    public boolean line(Token other) {
        return this.line == other.line;
    }


    public boolean isComment() { return this.kind == Kind.COMMENT_LINE || this.kind == Kind.COMMENT_BLOCK; }
    public boolean isSpaces() { return this.kind == Kind.SPACES; }
    public boolean isCR() { return this.kind == Kind.CR; }
    public boolean isSpacesOrCR() {
        return this.kind == Kind.SPACES || this.kind == Kind.CR;
    }
    public boolean isEOF() { return this.kind == Kind.EOF; }
    public boolean isDot() { return this.kind == Kind.DOT; }
    public boolean isColon() { return this.kind == Kind.COLON; }

    public boolean is(Kind kind) { return this.kind == kind; }

    public boolean is(Kind kind, Kind kind2) { return is(kind) || is(kind2); }

    public boolean is(Kind[] kinds) {
        for (Kind kind : kinds) {
            if (is(kind)) {
                return true;
            }
        }
        return false;
    }

    public boolean is(Iterable<Kind> kinds) {
        for (Kind kind : kinds) {
            if (is(kind)) {
                return true;
            }
        }
        return false;
    }

    public boolean not(Kind kind) { return this.kind != kind; }

    public boolean not(Kind kind, Kind kind2) { return not(kind) && not(kind2); }

    public boolean not(Kind[] kinds) {
        for (Kind kind : kinds) {
            if (is(kind)) {
                return false;
            }
        }
        return true;
    }

    public boolean not(Iterable<Kind> kinds) {
        for (Kind kind : kinds) {
            if (is(kind)) {
                return false;
            }
        }
        return true;
    }
}
