package canoe.parser.syntax.expression.call;

import canoe.lexer.Token;
import canoe.parser.syntax.expression.Expression;
import canoe.parser.syntax.expression.pair.ExpressionRound;

/**
 * @author dawn
 */
public class ExpressionFunctionCall implements Expression {

    private final Token id;

    private final ExpressionRound round;

    public ExpressionFunctionCall(Token id, ExpressionRound round) {
        this.id = id;
        this.round = round;
    }

    @Override
    public Token first() {
        return id;
    }

    @Override
    public Token last() {
        return round.last();
    }

    public Token getId() {
        return id;
    }

    public ExpressionRound getRound() {
        return round;
    }

    @Override
    public String toString() {
        return id.value() + round.toString();
    }
}
