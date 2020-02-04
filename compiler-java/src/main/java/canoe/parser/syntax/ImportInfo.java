package canoe.parser.syntax;


import canoe.parser.syntax.imports.ImportStatement;
import canoe.util.Dump;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author dawn
 */
public class ImportInfo implements Dump {

    private List<ImportStatement> info;

    public ImportInfo(List<ImportStatement> info) {
        this.info = info;
    }


    @Override
    public void dump(Consumer<String> print) {
        info.forEach(s ->s.dump(print));
    }
}
