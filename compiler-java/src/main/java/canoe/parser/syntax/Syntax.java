package canoe.parser.syntax;

import canoe.lexer.Tokens;

/**
 * @author dawn
 */
public class Syntax {

    private Tokens tokens;
    private PackageInfo packageInfo;

    public Syntax(Tokens tokens, PackageInfo packageInfo) {
        this.tokens = tokens;
        this.packageInfo = packageInfo;
    }

    public Tokens getTokens() {
        return tokens;
    }
}
