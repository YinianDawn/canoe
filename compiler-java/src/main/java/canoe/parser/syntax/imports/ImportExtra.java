package canoe.parser.syntax.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;

import java.util.List;

import static canoe.parser.syntax.imports.ImportUtil.checkRound;
import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class ImportExtra {

    private final Token EXTRA;
    private final Token LR;
    private final ImportAs first;
    private final List<ImportCommaAs> others;
    private final Token RR;

    public ImportExtra(Token symbol, Token lr, ImportAs first, List<ImportCommaAs> others, Token rr) {
        this.EXTRA = symbol;
        this.LR = lr;
        this.first = first;
        this.others = others;
        this.RR = rr;
    }

    public void make(Token symbol, String file) {
        if (null == EXTRA || EXTRA.not(Kind.EXTRA)) {
            panic("must be " + Kind.EXTRA.value, symbol, file);
        }
        checkRound(LR, RR, EXTRA, others, file);
        if (null == first) {
            panic("must has " + Kind.EXTRA.value + " statement", EXTRA, file);
        } else {
            first.make(EXTRA, file);
        }
        others.forEach(o -> o.make(EXTRA, file));
    }
}
