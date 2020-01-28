package canoe.parser.syntax.imports;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ImportSingle implements ImportStatement {

    private final Token IMPORT;

    private final ImportUnit unit;

    public ImportSingle(Token symbol, ImportUnit unit) {
        this.IMPORT = symbol;
        this.unit = unit;
    }

}
