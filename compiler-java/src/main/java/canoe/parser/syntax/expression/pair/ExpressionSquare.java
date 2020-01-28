package canoe.parser.syntax.expression.pair;

import canoe.lexer.Token;
import canoe.parser.syntax.expression.Expression;

/**
 * @author dawn
 */
public class ExpressionSquare implements Expression {

    private final Token LS;
    private final Expression expression;
    private final Token RS;

    public ExpressionSquare(Token ls, Expression expression, Token rs) {
        this.LS = ls;
        this.expression = expression;
        this.RS = rs;
    }

    @Override
    public Token first() {
        return LS;
    }

    @Override
    public Token last() {
        return RS;
    }
}
