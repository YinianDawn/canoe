package canoe2.ast;

import canoe2.ast.import_statement.ImportStatements;
import canoe2.ast.statement.Statements;

/**
 * @author dawn
 */
public class AST {

    private String fileName;
    private PackageName packageName;
    private ImportStatements importStatements;
    private Statements statements;

    public AST(String fileName, PackageName packageName, ImportStatements importStatements, Statements statements) {
        this.fileName = fileName;
        this.packageName = packageName;
        this.importStatements = importStatements;
        this.statements = statements;
    }

    public String getPackageName() {
        return packageName.getName();
    }

    public String getFileName() {
        return fileName;
    }
}
