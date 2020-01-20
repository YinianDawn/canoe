package canoe.ast.expression;

import canoe.lexis.Kind;
import canoe.lexis.Token;

/**
 * @author dawn
 */
public class ExpressionLambdaExpression implements Expression {

    private Expression params;

    private Token lambda;

    private Expression expression;

    public ExpressionLambdaExpression(Expression params, Token lambda, Expression expression) {
        this.params = params;
        this.lambda = lambda;
        this.expression = expression;
    }

    @Override
    public boolean endWith(Kind kind) {
        return expression.endWith(kind);
    }
}
