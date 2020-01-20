package canoe.ast.statement;

import canoe.ast.expression.Expression;

/**
 * @author dawn
 */
public class StatementExpression implements Statement {
    private Expression expression;

    public StatementExpression(Expression expression) {
        this.expression = expression;
    }

}
