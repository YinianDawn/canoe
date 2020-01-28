package canoe.parser.syntax.imports;

import canoe.lexer.Token;

import java.util.List;

/**
 * @author dawn
 */
public class ImportMany implements ImportStatement {

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

}
