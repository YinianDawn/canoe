package canoe.parser.syntax;

import canoe.lexer.Kind;
import canoe.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

import static canoe.lexer.KindSet.SINGLE_KEY_WORDS;
import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class PackageInfo implements Produce<PackageInfo> {

    private final Token symbol;
    private final List<Token> info;
    private final String name;

    public PackageInfo(Token symbol, List<Token> info) {
        this.symbol = symbol;
        this.info = info;
        this.name = info.stream().map(Token::value).collect(Collectors.joining());
    }

    public String getName() {
        return name;
    }

    @Override
    public PackageInfo make(String file) {
        if (null == symbol || symbol.not(Kind.PACKAGE)) {
            panic("must be package", file, symbol);
        }
        if (!info.isEmpty()) {
            boolean id = true;
            for (Token token : info) {
                if (id) {
                    if (token.not(Kind.ID) && token.not(SINGLE_KEY_WORDS)) {
                        panic("must be id", file, token);
                    }
                    id = false;
                } else {
                    if (token.not(Kind.DOT)) {
                        panic("must be .", file, token);
                    }
                    id = true;
                }
            }
            if (info.get(info.size() - 1).is(Kind.DOT) || info.get(info.size() - 1).is(SINGLE_KEY_WORDS)) {
                panic("last package name must be id", file, info.get(info.size() - 1));
            }
        }
        return this;
    }
}
