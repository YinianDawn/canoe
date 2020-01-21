package canoe2.ast.statement;

import canoe2.ast.expression.Expression;
import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class StatementOpAndAssign implements Statement {

    private Expression idExpression;

    private Token opAssign;

    private Expression expression;

    public StatementOpAndAssign(Expression idExpression, Token opAssign, Expression expression) {
        this.idExpression = idExpression;
        this.opAssign = opAssign;
        this.expression = expression;
    }
}
