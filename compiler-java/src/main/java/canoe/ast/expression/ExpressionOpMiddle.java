package canoe.ast.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionOpMiddle implements Expression {

    private Expression leftExpression;
    private Token middleOp;
    private Expression rightExpression;


    public ExpressionOpMiddle(Expression leftExpression, Token middleOp, Expression rightExpression) {
        this.leftExpression = leftExpression;
        this.middleOp = middleOp;
        this.rightExpression = rightExpression;
    }

    public Expression getLeftExpression() {
        return leftExpression;
    }

    @Override
    public Token first() {
        return leftExpression.first();
    }

    @Override
    public Token last() {
        return rightExpression.last();
    }
}
