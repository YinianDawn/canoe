package canoe.parser.syntax.expression.trait;


import canoe.lexer.Token;
import canoe.parser.syntax.expression.Expression;

/**
 * @author dawn
 */
public class ExpressionTraits implements Expression {

    private final ExpressionTraits traits;
    private final Token COLON;
    private final Expression expression;

    public ExpressionTraits(ExpressionTraits traits, Token colon, Expression expression) {
        this.traits = traits;
        this.COLON = colon;
        this.expression = expression;
    }

    @Override
    public Token first() {
        return null == traits ? COLON : traits.first();
    }

    @Override
    public Token last() {
        return expression.last();
    }
}
