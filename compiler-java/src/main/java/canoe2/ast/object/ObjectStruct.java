package canoe2.ast.object;

import canoe2.ast.statement.Statements;
import canoe2.lexis.Token;

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
