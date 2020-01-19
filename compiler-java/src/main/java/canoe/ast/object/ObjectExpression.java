package canoe.ast.object;

import canoe.ast.expression.Expression;

/**
 * @author dawn
 */
public class ObjectExpression extends Object {

    private Expression expression;

    public ObjectExpression(Expression expression) {
        this.expression = expression;
    }
}
