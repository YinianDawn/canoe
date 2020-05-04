package canoe.parser.syntax.expression.trait;


import canoe.lexer.Token;
import canoe.parser.syntax.expression.Expression;

/**
 * @author dawn
 */
public class ExpressionWithTraits implements Expression {

    private final Expression id;

    private final ExpressionTraits traits;

    public ExpressionWithTraits(Expression id, ExpressionTraits traits) {
        this.id = id;
        this.traits = traits;
    }

    public Expression getId() {
        return id;
    }

    public ExpressionTraits getTraits() {
        return traits;
    }

    @Override
    public Token first() {
        return id.first();
    }

    @Override
    public Token last() {
        return null == traits ? id.last() : traits.last();
    }
}
