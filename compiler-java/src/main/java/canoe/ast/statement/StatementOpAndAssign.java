package canoe.ast.statement;

import canoe.ast.expression.Expression;
import canoe.lexis.Token;

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
