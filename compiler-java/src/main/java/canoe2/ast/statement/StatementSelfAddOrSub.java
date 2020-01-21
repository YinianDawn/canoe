package canoe2.ast.statement;

import canoe2.ast.expression.Expression;
import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class StatementSelfAddOrSub implements Statement {

    private Expression idExpression;

    private Token rightOp;

    public StatementSelfAddOrSub(Expression idExpression, Token rightOp) {
        this.idExpression = idExpression;
        this.rightOp = rightOp;
    }
}
