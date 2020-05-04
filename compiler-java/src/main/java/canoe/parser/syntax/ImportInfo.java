package canoe.parser.syntax;


import canoe.parser.syntax.imports.ImportMany;
import canoe.parser.syntax.imports.ImportSingle;
import canoe.parser.syntax.imports.ImportStatement;
import canoe.parser.syntax.imports.ImportUnit;
import canoe.util.Dump;
import canoe.util.PanicUtil;

import java.util.LinkedList;
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

    public List<ImportUnit> get() {
        List<ImportUnit> singles = new LinkedList<>();
        info.forEach(is -> {
            if (is instanceof ImportSingle) {
                singles.add(((ImportSingle) is).getUnit());
            } else if (is instanceof ImportMany) {
                singles.addAll(((ImportMany) is).get());
            } else {
                PanicUtil.panic("can not be " + is);
            }
        });
        return singles;
    }

    @Override
    public void dump(Consumer<String> print) {
        info.forEach(s ->s.dump(print));
    }
}
