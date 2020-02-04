package canoe.parser.syntax;

import canoe.lexer.Token;
import canoe.util.Dump;

import java.util.function.Consumer;

/**
 * @author dawn
 */
public class PackageInfo implements Dump {

    private final Token PACKAGE;
    private final Token name;

    public PackageInfo(Token symbol, Token name) {
        this.PACKAGE = symbol;
        this.name = name;
    }

    @Override
    public void dump(Consumer<String> print) {
        print.accept(PACKAGE.kind.value + " " + name.value());
    }
}
