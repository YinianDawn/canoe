package canoe.ast.imports;

import canoe.lexer.Token;

import java.util.List;

/**
 * @author dawn
 */
public class ImportStatementMany implements ImportStatement {

    private Token importToken;
    private Token ls;
    private List<ImportStatementSingle> importStatementSingles;
    private Token rs;

    public ImportStatementMany(Token importToken, Token ls, List<ImportStatementSingle> importStatementSingles, Token rs) {
        this.importToken = importToken;
        this.ls = ls;
        this.importStatementSingles = importStatementSingles;
        this.rs = rs;
    }

    public List<ImportStatementSingle> getImportStatementSingles() {
        return importStatementSingles;
    }
}
