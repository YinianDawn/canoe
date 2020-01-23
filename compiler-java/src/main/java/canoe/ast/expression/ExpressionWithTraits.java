package canoe.ast.expression;


import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionWithTraits implements Expression {

    private Expression expressionID;

    private ExpressionTraits traits;

    public ExpressionWithTraits(Expression expressionID, ExpressionTraits traits) {
        this.expressionID = expressionID;
        this.traits = traits;
    }

    public Expression getExpressionID() {
        return expressionID;
    }

    public ExpressionTraits getTraits() {
        return traits;
    }

    @Override
    public Token first() {
        return expressionID.first();
    }

    @Override
    public Token last() {
        return null == traits ? expressionID.last() : traits.last();
    }
}
