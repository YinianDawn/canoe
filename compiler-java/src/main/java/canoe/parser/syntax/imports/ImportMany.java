package canoe.parser.syntax.imports;

import canoe.lexer.Token;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author dawn
 */
public class ImportMany implements ImportStatement {

    private final Token IMPORT;
    private final Token LR;
    private final List<ImportUnit> units;
    private final Token RR;

    public ImportMany(Token symbol, Token lr, List<ImportUnit> units, Token rr) {
        this.IMPORT = symbol;
        this.LR = lr;
        this.units = units;
        this.RR = rr;
    }

    @Override
    public void dump(Consumer<String> print) {
        print.accept(IMPORT.value() + LR.value() + units.stream().map(ImportUnit::toString).collect(Collectors.joining(";")) + RR.value());
    }
}
