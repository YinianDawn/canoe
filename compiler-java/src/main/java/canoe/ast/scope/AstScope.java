package canoe.ast.scope;

import canoe.ast.Ast;
import canoe.ast.stm.Exp;

/**
 * @author dawn
 */
public class AstScope {

    private Ast ast;
    private Exp exp;

    public AstScope(Ast ast, Exp exp) {
        this.ast = ast;
        this.exp = exp;
    }
}
