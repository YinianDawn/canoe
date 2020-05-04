package canoe.parser.syntax.expression.expression;


import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionCommaTraits implements Expression {

    private ExpressionTraits traits1;

    private Token comma;

    private Expression traits2;

    public ExpressionCommaTraits(ExpressionTraits traits1, Token comma, Expression traits2) {
        this.traits1 = traits1;
        this.comma = comma;
        this.traits2 = traits2;
    }

    @Override
    public Token first() {
        return traits1.first();
    }

    @Override
    public Token last() {
        return traits2.last();
    }
}
