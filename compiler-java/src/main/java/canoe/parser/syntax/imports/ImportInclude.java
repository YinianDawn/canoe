package canoe.parser.syntax.imports;

import canoe.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dawn
 */
public class ImportInclude {

    private final Token INCLUDE;
    private final Token LR;
    private final ImportId first;
    private final List<ImportCommaId> others;
    private final Token RR;

    public ImportInclude(Token symbol, Token lr, ImportId first, List<ImportCommaId> others, Token rr) {
        this.INCLUDE = symbol;
        this.LR = lr;
        this.first = first;
        this.others = others;
        this.RR = rr;
    }

    @Override
    public String toString() {
        if (null == LR) {
            return INCLUDE.value() + " " + first.toString();
        }
        return INCLUDE.value() + LR.value() + first.toString() + others.stream().map(Object::toString).collect(Collectors.joining())+ RR.value();
    }
}
