package canoe.ast;


import canoe.ast.imports.ImportStatements;
import canoe.ast.statement.Statements;
import canoe.lexer.Tokens;

/**
 * @author dawn
 */
public class AST {

    private Tokens tokens;

    private PackageInfo packageInfo;
    private ImportStatements importStatements;
    private Statements statements;

    public AST(Tokens tokens, PackageInfo packageInfo, ImportStatements importStatements, Statements statements) {
        this.tokens = tokens;
        this.packageInfo = packageInfo;
        this.importStatements = importStatements;
        this.statements = statements;
    }
}
