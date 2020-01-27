package canoe.ast.statement;

import canoe.ast.expression.Expression;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class StatementExpression implements Statement {
    private Expression expression;

    public StatementExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Token first() {
        return expression.first();
    }

    @Override
    public Token last() {
        return expression.last();
    }
}
