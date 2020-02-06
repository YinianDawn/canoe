package canoe.parser.syntax;

import canoe.lexer.Tokens;

/**
 * @author dawn
 */
public class ConcreteSyntax {

    private Tokens tokens;
    private PackageInfo packageInfo;
    private ImportInfo importInfo;
    private Statements statements;

    public ConcreteSyntax(Tokens tokens, PackageInfo packageInfo, ImportInfo importInfo, Statements statements) {
        this.tokens = tokens;
        this.packageInfo = packageInfo;
        this.importInfo = importInfo;
        this.statements = statements;
    }

    public Tokens getTokens() {
        return tokens;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public ImportInfo getImportInfo() {
        return importInfo;
    }

    public Statements getStatements() {
        return statements;
    }
}
