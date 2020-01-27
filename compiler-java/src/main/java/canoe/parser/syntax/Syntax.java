package canoe.parser.syntax;

import canoe.lexer.Tokens;

/**
 * @author dawn
 */
public class Syntax {

    private Tokens tokens;
    private PackageInfo packageInfo;
    private ImportInfo importInfo;

    public Syntax(Tokens tokens, PackageInfo packageInfo, ImportInfo importInfo) {
        this.tokens = tokens;
        this.packageInfo = packageInfo;
        this.importInfo = importInfo;
    }

    public Tokens getTokens() {
        return tokens;
    }
}
