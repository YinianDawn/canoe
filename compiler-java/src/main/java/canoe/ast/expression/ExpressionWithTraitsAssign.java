package canoe.ast.expression;


import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionWithTraitsAssign implements Expression {

    private Expression expressionID;

    private ExpressionTraits traits;

    private Token assign;

    private Expression expression;

    public ExpressionWithTraitsAssign(Expression expressionID, ExpressionTraits traits, Token assign, Expression expression) {
        this.expressionID = expressionID;
        this.traits = traits;
        this.assign = assign;
        this.expression = expression;
    }

    @Override
    public Token first() {
        return expressionID.first();
    }

    @Override
    public Token last() {
        return expression.last();
    }
}
