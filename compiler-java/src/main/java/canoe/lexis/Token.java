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

    int getLength() {
        return length;
    }

    public Kind getKind() {
        return kind;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return String.format("%s l:%d i:%d l:%d v:%s",
                kind.name(), line, index, length, (null == value ? kind.getKey() : value));
    }

    public boolean next(Token next) {
        return this.line == next.line && this.index + this.length == next.index;
    }

}
