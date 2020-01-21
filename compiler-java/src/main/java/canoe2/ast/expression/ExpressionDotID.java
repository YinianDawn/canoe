package canoe2.ast.expression;

import canoe2.lexis.Kind;
import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class ExpressionDotID implements Expression {

    private Expression expression;

    private Token dot;

    private ExpressionID expressionID;

    public ExpressionDotID(Expression expression, Token dot, ExpressionID expressionID) {
        this.expression = expression;
        this.dot = dot;
        this.expressionID = expressionID;
    }

    @Override
    public boolean endWith(Kind kind) {
        return expressionID.endWith(kind);
    }
}
