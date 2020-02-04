package canoe.parser.syntax;


import canoe.parser.syntax.imports.ImportStatement;

import java.util.List;

/**
 * @author dawn
 */
public class ImportInfo {

    private List<ImportStatement> info;

    public ImportInfo(List<ImportStatement> info) {
        this.info = info;
    }


}
