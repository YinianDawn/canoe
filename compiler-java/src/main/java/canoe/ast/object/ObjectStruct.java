package canoe.ast.object;

import canoe.ast.statement.Statements;

/**
 * @author dawn
 */
public class ObjectStruct extends Object {

    private Statements statements;

    public ObjectStruct(Statements statements) {
        this.statements = statements;
    }
}
