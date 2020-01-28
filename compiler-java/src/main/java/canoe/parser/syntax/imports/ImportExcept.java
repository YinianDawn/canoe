package canoe.parser.syntax.imports;

import canoe.lexer.Token;

import java.util.List;

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

}