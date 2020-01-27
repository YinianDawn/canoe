package canoe.parser.syntax.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.syntax.PackageInfo;
import canoe.parser.syntax.Produce;

import java.util.List;
import java.util.stream.Collectors;

import static canoe.lexer.KindSet.SINGLE_KEY_WORDS;
import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class ImportSingle implements ImportStatement, Produce<ImportSingle> {

    private final Token symbol;

    private final List<Token> info;
    private final Token as;
    private final Token id;

    private final String names;
    private final String name;

    public ImportSingle(Token symbol, List<Token> info, Token as, Token id) {
        this.symbol = symbol;
        this.info = info;
        this.as = as;
        this.id = id;

        this.names = info.stream().map(Token::value).collect(Collectors.joining());
        this.name = null == id ? info.get(info.size() - 1).value() : id.value();
    }

    @Override
    public ImportSingle make(String file) {
        if (null == symbol || symbol.not(Kind.IMPORT)) {
            panic("must be " + Kind.IMPORT.value, symbol, file);
        }
        if (info.isEmpty()) {
            panic(Kind.IMPORT.value + " " + Kind.PACKAGE.value + " name can not be empty", symbol, file);
        }
        PackageInfo.checkPackage(info, file);
        if (null != as) {
            if (null == id) {
                panic("must has id after as.", as, file);
            } else if (id.not(Kind.ID)) {
                panic("must be id.", id, file);
            }
        }

        return this;
    }
    public static void checkPackage(List<Token> info, String file) {
        if (!info.isEmpty()) {
            boolean id = true;
            for (Token token : info) {
                if (id) {
                    if (token.not(Kind.ID) && token.not(SINGLE_KEY_WORDS)) {
                        panic("must be id", token, file);
                    }
                    id = false;
                } else {
                    if (token.not(Kind.DOT)) {
                        panic("must be .", token, file);
                    }
                    id = true;
                }
            }
            if (info.get(info.size() - 1).is(Kind.DOT) || info.get(info.size() - 1).is(SINGLE_KEY_WORDS)) {
                panic("last " + Kind.PACKAGE.value + " name must be id", info.get(info.size() - 1), file);
            }
        }
    }
}
