package canoe.parser.syntax;

import canoe.parser.syntax.imports.ImportStatement;

import java.util.List;

/**
 * @author dawn
 */
public class ImportInfo implements Produce<ImportInfo> {

    private List<ImportStatement> info;

    public ImportInfo(List<ImportStatement> info) {
        this.info = info;
    }

    @Override
    public ImportInfo make(String file) {
        info.forEach(i -> i.make(file));
        return this;
    }

}
