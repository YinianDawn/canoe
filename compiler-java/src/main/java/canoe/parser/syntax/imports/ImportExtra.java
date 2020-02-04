package canoe.parser.syntax.imports;

import canoe.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        if (null == LR) {
            return EXTRA.value() + " " + first.toString();
        }
        return EXTRA.value() + LR.value() + first.toString() + others.stream().map(Object::toString).collect(Collectors.joining())+ RR.value();
    }
}
