package canoe.parser.syntax.imports;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ImportCommaId {

    private final Token COMMA;
    private final ImportId importId;

    public ImportCommaId(Token comma, ImportId importId) {
        this.COMMA = comma;
        this.importId = importId;
    }
    @Override
    public String toString() {
        return COMMA.value() + importId.toString();
    }
}
