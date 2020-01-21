package canoe.ast;

import canoe.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dawn
 */
public class PackageInfo {

    private final Token packageToken;

    private final List<Token> names;

    private final String name;

    public PackageInfo(Token packageToken, List<Token> names) {
        this.packageToken = packageToken;
        this.names = names;
        this.name = names.stream().map(Token::getValue).collect(Collectors.joining());
    }

    public String getName() {
        return name;
    }

}
