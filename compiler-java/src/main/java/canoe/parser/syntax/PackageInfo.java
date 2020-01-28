package canoe.parser.syntax;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class PackageInfo {

    private final Token PACKAGE;
    private final Token name;

    public PackageInfo(Token symbol, Token name) {
        this.PACKAGE = symbol;
        this.name = name;
    }

    public String getName() {
        return name.value();
    }

}
