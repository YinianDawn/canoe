package canoe.parser.syntax.statement;

import canoe.lexer.Token;
import canoe.parser.syntax.expression.Expression;

/**
 * @author dawn
 */
public class StatementExpression implements Statement {

    private final Expression expression;

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

    @Override
    public String toString() {
        return expression.toString();
    }
}
