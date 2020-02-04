package canoe.parser.syntax.imports;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ImportUnit {

    private final Token path;
    private final Token AS;
    private final Token id;
    private final ImportExtra extra;
    private final ImportExcept except;

    public ImportUnit(Token path, Token as, Token id, ImportExtra extra, ImportExcept except) {
        this.path = path;
        this.AS = as;
        this.id = id;
        this.extra = extra;
        this.except = except;
    }

    @Override
    public String toString() {
        return String.format("\"%s\"%s%s%s", path.value(),
                null == AS ? "" : (" " + AS.kind.value + " " + id.value()),
                null == extra ? "" : extra.toString(),
                null == except ? "" : except.toString());
    }
}
