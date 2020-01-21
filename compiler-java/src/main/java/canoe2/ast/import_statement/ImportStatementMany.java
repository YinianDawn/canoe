package canoe.ast.import_statement;

import java.util.List;

/**
 * @author dawn
 */
public class ImportStatementMany extends ImportStatement {
    private List<ImportStatementSingle> importStatementSingles;

    public ImportStatementMany(List<ImportStatementSingle> importStatementSingles) {
        this.importStatementSingles = importStatementSingles;
    }
}
