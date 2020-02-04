package canoe.parser.syntax.imports;

import canoe.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

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
    @Override
    public String toString() {
        if (null == LR) {
            return EXCEPT.value() + " " + first.toString();
        }
        return EXCEPT.value() + LR.value() + first.toString() + others.stream().map(Object::toString).collect(Collectors.joining())+ RR.value();
    }
}
