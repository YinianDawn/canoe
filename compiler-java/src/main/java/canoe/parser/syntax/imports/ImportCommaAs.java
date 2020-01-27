package canoe.parser.syntax.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class ImportCommaAs {

    private final Token COMMA;
    private final ImportAs as;

    public ImportCommaAs(Token comma, ImportAs as) {
        this.COMMA = comma;
        this.as = as;
    }

    public void make(Token symbol, String file) {
        if (null == COMMA || COMMA.not(Kind.COMMA)) {
            panic("must has , " + Kind.COMMA.value, symbol, file);
        }
        if (null == as) {
            panic("must has something ", COMMA, file);
        } else {
            as.make(COMMA, file);
        }
    }
}
