package canoe2.ast.expression;

import canoe2.lexis.Kind;
import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class ExpressionDotFunction implements Expression {

    private Expression expression;

    private Token dot;

    private ExpressionFunction expressionFunction;

    public ExpressionDotFunction(Expression expression, Token dot, ExpressionFunction expressionFunction) {
        this.expression = expression;
        this.dot = dot;
        this.expressionFunction = expressionFunction;
    }

    @Override
    public boolean endWith(Kind kind) {
        return expressionFunction.endWith(kind);
    }
}
