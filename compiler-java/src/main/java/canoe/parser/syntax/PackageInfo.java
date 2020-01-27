package canoe.parser.syntax;

import canoe.lexer.Kind;
import canoe.lexer.Token;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class PackageInfo implements Produce<PackageInfo> {

    private final Token PACKAGE;
    private final Token name;

    public PackageInfo(Token symbol, Token name) {
        this.PACKAGE = symbol;
        this.name = name;
    }

    public String getName() {
        return name.value();
    }

    @Override
    public PackageInfo make(String file) {
        if (null == PACKAGE || PACKAGE.not(Kind.PACKAGE)) {
            panic("must be " + Kind.PACKAGE.value, PACKAGE, file);
        }
        if (null == name || name.not(Kind.ID)) {
            panic(Kind.PACKAGE.value + " name must be ID", name, file);
        }
        return this;
    }

}
