package canoe.parser.syntax.imports;

import canoe.lexer.Token;
import canoe.parser.syntax.Produce;

import java.util.List;

/**
 * @author dawn
 */
public class ImportMany implements ImportStatement, Produce<ImportMany> {

    private final Token importToken;
    private final Token LR;
    private final List<ImportSingle> importStatementSingles;
    private final Token RR;

    public ImportMany(Token importToken, Token lr, List<ImportSingle> importStatementSingles, Token rr) {
        this.importToken = importToken;
        this.LR = lr;
        this.importStatementSingles = importStatementSingles;
        this.RR = rr;
    }

    public List<ImportSingle> getImportStatementSingles() {
        return importStatementSingles;
    }

    @Override
    public ImportMany make(String file) {
        return this;
    }
}
