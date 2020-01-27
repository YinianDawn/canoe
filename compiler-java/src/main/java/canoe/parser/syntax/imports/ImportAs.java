package canoe.parser.syntax.imports;

import canoe.lexer.Token;

import java.util.List;

import static canoe.parser.syntax.imports.ImportUtil.checkAs;
import static canoe.parser.syntax.imports.ImportUtil.checkInfo;
import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class ImportAs {

    private final List<Token> info;
    private final Token AS;
    private final Token id;

    public ImportAs(List<Token> info, Token symbol, Token id) {
        this.info = info;
        this.AS = symbol;
        this.id = id;
    }

    public void make(Token symbol, String file) {
        if (null == info || info.isEmpty()) {
            panic("must has something", symbol, file);
        } else {
            checkInfo(info, file);
        }
        checkAs(AS, id, file);
    }



}
