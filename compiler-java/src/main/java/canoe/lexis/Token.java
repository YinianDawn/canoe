package canoe.lexis;

/**
 * @author dawn
 */
public class Token {

    private final Kind kind;

    private final String value;

    private final int line;

    private final int index;

    private final int length;

    public Token(Kind kind, String value, int line, int index, int length) {
        this.kind = kind;
        this.value = value;
        this.line = line;
        this.index = index;
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public Kind getKind() {
        return kind;
    }

    public String getValue() {
        return null == value ? kind.getKey() : value;
    }

    public int getLine() {
        return line;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return String.format("%s [%d:%d:%d] %s", kind.name(), line, index, length, getValue());
    }

    public boolean next(Token next) {
        return this.line == next.line && this.index + this.length == next.index;
    }

    public boolean comment() {
        return this.kind == Kind.COMMENT_LINE || this.kind == Kind.COMMENT_BLOCK;
    }

    public boolean isSpaces() {
        return this.kind == Kind.SPACES;
    }
    public boolean isCR() {
        return this.kind == Kind.CR;
    }
}
