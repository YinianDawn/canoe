package canoe.parser.syntax.expression.trait;


import canoe.lexer.Token;
import canoe.parser.syntax.expression.Expression;

/**
 * @author dawn
 */
public class ExpressionWithTraitsAssign implements Expression {

    private final Expression id;
    private final ExpressionTraits traits;
    private final Token assign;
    private final Expression expression;

    public ExpressionWithTraitsAssign(Expression id, ExpressionTraits traits, Token assign, Expression expression) {
        this.id = id;
        this.traits = traits;
        this.assign = assign;
        this.expression = expression;
    }

    @Override
    public Token first() {
        return id.first();
    }

    @Override
    public Token last() {
        return expression.last();
    }
}
