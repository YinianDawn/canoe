package canoe.ast.expression;

import canoe.lexis.Kind;
import canoe.lexis.Token;

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
