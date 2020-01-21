package canoe.ast.object;

import canoe.ast.expression.Expression;

/**
 * @author dawn
 */
public class ObjectExpression implements Object {

    private Expression expression;

    public ObjectExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
