package canoe.parser.syntax.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class ImportSingle implements ImportStatement<ImportSingle> {

    private final Token IMPORT;

    private final ImportUnit unit;

    public ImportSingle(Token symbol, ImportUnit unit) {
        this.IMPORT = symbol;
        this.unit = unit;
    }

    @Override
    public ImportSingle make(String file) {
        if (null == IMPORT || IMPORT.not(Kind.IMPORT)) {
            panic("must be " + Kind.IMPORT.value, IMPORT, file);
        }
        unit.make(IMPORT, file);
        return this;
    }
}
