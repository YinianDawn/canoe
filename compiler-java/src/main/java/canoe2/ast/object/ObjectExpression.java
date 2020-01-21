package canoe2.ast.object;

import canoe2.ast.expression.Expression;

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
