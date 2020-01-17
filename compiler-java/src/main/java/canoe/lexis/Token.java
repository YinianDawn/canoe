package canoe.lexis;

/**
 * @author dawn
 */
public class Token {

    private final Kind kind;

    private final String value;

    private final String fileName;

    private final int lineNumber;

    private final int index;

    private final int length;

    Token(Kind kind, String value, String fileName, int lineNumber, int index, int length) {
        this.kind = kind;
        this.value = value;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.index = index;
        this.length = length;
    }

    int getLength() {
        return length;
    }

    public Kind getKind() {
        return kind;
    }

    public int getIndex() {
        return index;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return String.format("%s value:%s line:%d index:%d length:%d",
                kind.name(), (null == value ? kind.getKey() : value), lineNumber, index, length);
    }
}
