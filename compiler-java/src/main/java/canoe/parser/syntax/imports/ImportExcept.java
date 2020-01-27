package canoe.parser.syntax.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;

import java.util.List;

import static canoe.parser.syntax.imports.ImportUtil.checkRound;
import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class ImportExcept {

    private final Token EXCEPT;
    private final Token LR;
    private final ImportAs first;
    private final List<ImportCommaAs> others;
    private final Token RR;

    public ImportExcept(Token symbol, Token lr, ImportAs first, List<ImportCommaAs> others, Token rr) {
        this.EXCEPT = symbol;
        this.LR = lr;
        this.first = first;
        this.others = others;
        this.RR = rr;
    }

    public void make(Token symbol, String file) {
        if (null == EXCEPT || EXCEPT.not(Kind.EXCEPT)) {
            panic("must be " + Kind.EXCEPT.value, symbol, file);
        }
        checkRound(LR, RR, EXCEPT, others, file);
        if (null == first) {
            panic("must has " + Kind.EXCEPT.value + " statement", EXCEPT, file);
        } else {
            first.make(EXCEPT, file);
        }
        others.forEach(o -> o.make(EXCEPT, file));
    }
}
