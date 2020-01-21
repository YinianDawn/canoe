package canoe.ast.imports;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class ImportStatements {

    private List<ImportStatement> importStatements;

    private List<ImportStatementSingle> importStatementSingles;

    public ImportStatements(List<ImportStatement> importStatements) {
        this.importStatements = importStatements;
        List<ImportStatementSingle> temp = new LinkedList<>();
        for (ImportStatement statement : importStatements) {
            if (statement instanceof ImportStatementMany) {
                temp.addAll(((ImportStatementMany) statement).getImportStatementSingles());
            } else if (statement instanceof ImportStatementSingle){
                temp.add((ImportStatementSingle) statement);
            } else {
                panic("wrong import statement: " + statement);
            }
        }
        this.importStatementSingles = new ArrayList<>(temp);
    }

}
