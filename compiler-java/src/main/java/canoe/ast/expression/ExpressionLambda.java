package canoe.ast.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionLambda implements Expression {

    private Expression params;
    private Token lambda;
    private Expression expression;

    public ExpressionLambda(Expression params, Token lambda, Expression expression) {
        this.params = params;
        this.lambda = lambda;
        this.expression = expression;
    }

    @Override
    public Token first() {
        return params.first();
    }

    @Override
    public Token last() {
        return expression.last();
    }
}
