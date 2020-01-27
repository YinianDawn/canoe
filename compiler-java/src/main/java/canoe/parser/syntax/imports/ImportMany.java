package canoe.parser.syntax.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;

import java.util.List;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class ImportMany implements ImportStatement<ImportMany> {

    private final Token IMPORT;
    private final Token LR;
    private final List<ImportUnit> units;
    private final Token RR;

    public ImportMany(Token symbol, Token lr, List<ImportUnit> units, Token rr) {
        this.IMPORT = symbol;
        this.LR = lr;
        this.units = units;
        this.RR = rr;
    }

    @Override
    public ImportMany make(String file) {
        if (null == IMPORT || IMPORT.not(Kind.IMPORT)) {
            panic("must be " + Kind.IMPORT.value, IMPORT, file);
        }
        if (null == LR || null == RR) {
            panic("wrong " + Kind.IMPORT.value + " statement about ( or )", IMPORT, file);
        }
        units.forEach(u -> u.make(IMPORT, file));
        return this;
    }
}
