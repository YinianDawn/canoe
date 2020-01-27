package canoe.parser.syntax.imports;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class ImportStatements {

    private List<ImportStatement> importStatements;

    private List<ImportSingle> importStatementSingles;

    public ImportStatements(List<ImportStatement> importStatements) {
        this.importStatements = importStatements;
        List<ImportSingle> temp = new LinkedList<>();
        for (ImportStatement statement : importStatements) {
            if (statement instanceof ImportMany) {
                temp.addAll(((ImportMany) statement).getImportStatementSingles());
            } else if (statement instanceof ImportSingle){
                temp.add((ImportSingle) statement);
            } else {
                panic("wrong import statement: " + statement);
            }
        }
        this.importStatementSingles = new ArrayList<>(temp);
    }

}
