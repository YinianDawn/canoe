package canoe.ast.expression;


import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionAssign implements Expression {

    private Expression expression;

    private Token assign;

    private Expression expressionValue;

    public ExpressionAssign(Expression expression, Token assign, Expression expressionValue) {
        this.expression = expression;
        this.assign = assign;
        this.expressionValue = expressionValue;
    }

    @Override
    public Token first() {
        return expression.first();
    }

    @Override
    public Token last() {
        return expressionValue.last();
    }
}
