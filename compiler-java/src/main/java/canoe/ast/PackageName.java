package canoe.ast;

import canoe.lexis.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dawn
 */
public class PackageName {

    private final Token packageName;

    private final List<Token> names;

    private final String name;

    public PackageName(Token packageName, List<Token> names) {
        this.packageName = packageName;
        this.names = names;
        this.name = names.stream().map(Token::getValue).collect(Collectors.joining());
    }

    public String getName() {
        return name;
    }
}
