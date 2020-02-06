package canoe.parser.syntax.imports;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ImportUnit {

    private final Token id;
    private final Token path;
    private final ImportInclude extra;
    private final ImportExclude except;

    public ImportUnit(Token id, Token path, ImportInclude extra, ImportExclude except) {
        this.id = id;
        this.path = path;
        this.extra = extra;
        this.except = except;
    }

    @Override
    public String toString() {
        return String.format("%s\"%s\"%s%s",
                null == id ? "" : id.value() + " ",
                path.value(),
                null == extra ? "" : extra.toString(),
                null == except ? "" : except.toString());
    }
}
