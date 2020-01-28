package canoe.parser.syntax.expression.pair;

import canoe.lexer.Token;
import canoe.parser.syntax.expression.Expression;

/**
 * @author dawn
 */
public class ExpressionRound implements Expression {

    private final Token LR;
    private final Expression expression;
    private final Token RR;

    public ExpressionRound(Token lr, Expression expression, Token rr) {
        this.LR = lr;
        this.expression = expression;
        this.RR = rr;
    }

    @Override
    public Token first() {
        return LR;
    }

    @Override
    public Token last() {
        return RR;
    }
}
