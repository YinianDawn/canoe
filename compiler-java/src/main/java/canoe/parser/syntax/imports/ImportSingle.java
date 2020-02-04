package canoe.parser.syntax.imports;

import canoe.lexer.Token;

import java.util.function.Consumer;

/**
 * @author dawn
 */
public class ImportSingle implements ImportStatement {

    private final Token IMPORT;

    private final ImportUnit unit;

    public ImportSingle(Token symbol, ImportUnit unit) {
        this.IMPORT = symbol;
        this.unit = unit;
    }

    @Override
    public void dump(Consumer<String> print) {
        print.accept(IMPORT.kind.value + " " + unit.toString());
    }
}
