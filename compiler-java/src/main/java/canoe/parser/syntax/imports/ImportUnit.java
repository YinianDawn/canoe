package canoe.parser.syntax.imports;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ImportUnit {

    private final Token id;
    private final Token path;
    private final ImportInclude include;
    private final ImportExclude exclude;

    public ImportUnit(Token id, Token path, ImportInclude include, ImportExclude exclude) {
        this.id = id;
        this.path = path;
        this.include = include;
        this.exclude = exclude;
    }

    public Token getId() {
        return id;
    }

    public Token getPath() {
        return path;
    }

    public ImportInclude getInclude() {
        return include;
    }

    public ImportExclude getExclude() {
        return exclude;
    }

    @Override
    public String toString() {
        return String.format("%s\"%s\"%s%s",
                null == id ? "" : id.value() + " ",
                path.value(),
                null == include ? "" : include.toString(),
                null == exclude ? "" : exclude.toString());
    }
}
