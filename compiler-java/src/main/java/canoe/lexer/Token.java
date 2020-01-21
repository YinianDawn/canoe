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
        if (null == value && null == kind.getSign()) {
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
            case SPACES: case CR: case EOF: return;
            default:
        }
        if (null != kind.getSign() && size != kind.getSign().length()) {
            panic("size of " + kind.name() + " should be " + kind.getSign().length() + ".");
        }
    }

    public String getValue() {
        return null == value ? kind.getSign() : value;
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

    public boolean isComment() {
        return this.kind == Kind.COMMENT_LINE || this.kind == Kind.COMMENT_BLOCK;
    }

    public boolean isSpaces() {
        return this.kind == Kind.SPACES;
    }

    public boolean isCR() {
        return this.kind == Kind.CR;
    }

    public boolean isSpacesOrCR() {
        return this.kind == Kind.SPACES || this.kind == Kind.CR;
    }

    public boolean isEOF() { return this.kind == Kind.EOF; }

    public boolean isDot() { return this.kind == Kind.DOT; }

    public boolean is(Kind kind) {
        return this.kind == kind;
    }

    public boolean not(Kind kind) {
        return this.kind != kind;
    }

}
