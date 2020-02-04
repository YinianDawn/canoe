package canoe.ast;

import canoe.parser.syntax.Syntax;

/**
 * @author dawn
 */
public class AST {

    private Syntax syntax;

    public static AST parseAST(Syntax syntax) {
        return new AST(syntax);
    }

    private AST(Syntax syntax) {
        this.syntax = syntax;
    }




}
