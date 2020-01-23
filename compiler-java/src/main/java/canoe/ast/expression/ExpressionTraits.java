package canoe.ast.expression;


import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionTraits implements Expression {

    private ExpressionTraits traits1;

    private ExpressionTrait trait2;

    public ExpressionTraits(ExpressionTraits traits1, ExpressionTrait trait2) {
        this.traits1 = traits1;
        this.trait2 = trait2;
    }

    @Override
    public Token first() {
        return null == traits1 ? trait2.first() : traits1.first();
    }

    @Override
    public Token last() {
        return trait2.last();
    }
}
