package canoe.parser.syntax.imports;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ImportCommaAs {

    private final Token COMMA;
    private final ImportAs as;

    public ImportCommaAs(Token comma, ImportAs as) {
        this.COMMA = comma;
        this.as = as;
    }

}
