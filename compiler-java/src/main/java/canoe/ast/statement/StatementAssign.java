package canoe.ast.statement;

import canoe.ast.expression.Expression;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class StatementAssign implements Statement {

    private Expression expressionId;

    private Token assign;

    private Expression expression;

    public StatementAssign(Expression expressionId, Token assign, Expression expression) {
        this.expressionId = expressionId;
        this.assign = assign;
        this.expression = expression;
    }
}
