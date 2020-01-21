package canoe2.ast.statement;

import canoe2.ast.expression.Expression;

/**
 * @author dawn
 */
public class StatementExpression implements Statement {
    private Expression expression;

    public StatementExpression(Expression expression) {
        this.expression = expression;
    }

}
