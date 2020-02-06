package canoe.ast;

import canoe.lexer.Token;
import canoe.parser.syntax.ImportInfo;
import canoe.parser.syntax.Syntax;

/**
 * @author dawn
 */
public class Ast {

    private Syntax syntax;

    public static Ast parseAST(Syntax syntax) {
        return new Ast(syntax);
    }

    private Ast(Syntax syntax) {
        this.syntax = syntax;
        init();
    }

    private Token packageName;

    private ImportInfo importInfo;

//    private LinkedHashMap<>

    private void init() {
        packageName = syntax.getPackageInfo().getName();
        importInfo = syntax.getImportInfo();


    }



}
