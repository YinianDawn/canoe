package canoe.ast.object;

import canoe.ast.statement.Statements;
import canoe.lexis.Token;

/**
 * @author dawn
 */
public class ObjectStruct implements Object {

    private Token lb;

    private Statements statements;

    private Token rb;

    public ObjectStruct(Token lb, Statements statements, Token rb) {
        this.lb = lb;
        this.statements = statements;
        this.rb = rb;
    }
}
