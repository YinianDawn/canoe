package canoe.parser.syntax;

import canoe.lexer.Tokens;

/**
 * @author dawn
 */
public class Syntax {

    private Tokens tokens;
    private PackageInfo packageInfo;
    private ImportInfo importInfo;
    private Statements statements;

    public Syntax(Tokens tokens, PackageInfo packageInfo, ImportInfo importInfo, Statements statements) {
        this.tokens = tokens;
        this.packageInfo = packageInfo;
        this.importInfo = importInfo;
        this.statements = statements;
    }

    public Tokens getTokens() {
        return tokens;
    }
}
