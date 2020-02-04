package canoe.ast.expression;


import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionOperatorOverload implements Expression {

    private Token operator;

    private ExpressionTraits traits;

    private Token assign;

    private Expression expressionValue;

    public ExpressionOperatorOverload(Token operator, ExpressionTraits traits, Token assign, Expression expressionValue) {
        this.operator = operator;
        this.traits = traits;
        this.assign = assign;
        this.expressionValue = expressionValue;
    }

    @Override
    public Token first() {
        return operator;
    }

    @Override
    public Token last() {
        return expressionValue.last();
    }
}
