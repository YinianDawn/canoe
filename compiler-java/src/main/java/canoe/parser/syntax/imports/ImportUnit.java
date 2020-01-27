package canoe.parser.syntax.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;

import static canoe.parser.syntax.imports.ImportUtil.checkAs;
import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class ImportUnit {

    private final Token path;
    private final Token AS;
    private final Token id;
    private final ImportExtra extra;
    private final ImportExcept except;

    public ImportUnit(Token path, Token AS, Token id, ImportExtra extra, ImportExcept except) {
        this.path = path;
        this.AS = AS;
        this.id = id;
        this.extra = extra;
        this.except = except;
    }

    public void make(Token symbol, String file) {
        if (null == path || path.not(Kind.STRING)) {
            panic("path can not be empty", symbol, file);
        }
        checkAs(AS, id, file);
        if (null != extra) {
            extra.make(path, file);
        }
        if (null != except) {
            except.make(path, file);
        }
        if (null != extra && null != except) {
            panic("one of extra and except must be empty", AS, file);
        }
    }

}
