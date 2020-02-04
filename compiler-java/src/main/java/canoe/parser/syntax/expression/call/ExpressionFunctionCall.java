package canoe.parser.syntax.expression.call;

import canoe.lexer.Token;
import canoe.parser.syntax.expression.Expression;
import canoe.parser.syntax.expression.pair.ExpressionRound;

/**
 * @author dawn
 */
public class ExpressionFunctionCall implements Expression {

    private final Expression id;

    private final ExpressionRound round;

    public ExpressionFunctionCall(Expression id, ExpressionRound round) {
        this.id = id;
        this.round = round;
    }

    @Override
    public Token first() {
        return id.first();
    }

    @Override
    public Token last() {
        return round.last();
    }

    @Override
    public String toString() {
        return id.toString() + round.toString();
    }
}
