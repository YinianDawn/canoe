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
    private String name;

    public PackageInfo(Token symbol, List<Token> info) {
        this.symbol = symbol;
        this.info = info;
    }

    public String getName() {
        return name;
    }

    @Override
    public PackageInfo make(String file) {
        if (null == symbol || symbol.not(Kind.PACKAGE)) {
            panic("must be " + Kind.PACKAGE.value, symbol, file);
        }
        if (!info.isEmpty() && null != symbol) {
            if (!symbol.line(info.get(0))) {
                panic(Kind.PACKAGE.value + "name must be same line with key word " + Kind.PACKAGE.value, info.get(0), file);
            }
            if (symbol.position + symbol.size + 1 != info.get(0).position) {
                panic("wrong position.", info.get(0), file);
            }
        }
        checkPackage(info, file);
        this.name = info.stream().map(Token::value).collect(Collectors.joining());
        return this;
    }

    public static void checkPackage(List<Token> info, String file) {
        if (!info.isEmpty()) {
            Token last = null;
            boolean id = true;
            for (Token token : info) {
                if (null != last && !last.next(token)) {
                    panic(Kind.PACKAGE.value + " name must be same line", token, file);
                }
                if (id) {
                    if (token.not(Kind.ID) && token.not(SINGLE_KEY_WORDS)) {
                        panic("must be " + Kind.ID.name(), token, file);
                    }
                    id = false;
                } else {
                    if (token.not(Kind.DOT)) {
                        panic("must be .", token, file);
                    }
                    id = true;
                }
                last = token;
            }
            if (info.get(info.size() - 1).is(Kind.DOT) || info.get(info.size() - 1).is(SINGLE_KEY_WORDS)) {
                panic("last " + Kind.PACKAGE.value + " name must be id", info.get(info.size() - 1), file);
            }
        }
    }

}
