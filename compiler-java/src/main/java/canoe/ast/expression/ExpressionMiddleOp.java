package canoe.ast.expression;

import canoe.lexis.Kind;
import canoe.lexis.Token;

/**
 * @author dawn
 */
public class ExpressionMiddleOp implements Expression {

    private Expression leftExpression;
    private Token middleOp;
    private Expression rightExpression;


    public ExpressionMiddleOp(Expression leftExpression, Token middleOp, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.middleOp = middleOp;
        this.rightExpression = rightExpression;
    }

    @Override
    public boolean endWith(Kind kind) {
        return rightExpression.endWith(kind);
    }
}
