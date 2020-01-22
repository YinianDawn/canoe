package canoe.ast.imports;

import canoe.lexer.Token;

import java.util.List;

/**
 * @author dawn
 */
public class ImportStatementMany implements ImportStatement {

    private Token importToken;
    private Token lr;
    private List<ImportStatementSingle> importStatementSingles;
    private Token rr;

    public ImportStatementMany(Token importToken, Token lr, List<ImportStatementSingle> importStatementSingles, Token rr) {
        this.importToken = importToken;
        this.lr = lr;
        this.importStatementSingles = importStatementSingles;
        this.rr = rr;
    }

    public List<ImportStatementSingle> getImportStatementSingles() {
        return importStatementSingles;
    }
}
