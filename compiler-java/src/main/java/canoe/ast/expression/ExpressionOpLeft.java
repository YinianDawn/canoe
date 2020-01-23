package canoe.ast.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionOpLeft implements Expression {

    private Token leftOp;
    private Expression expression;


    public ExpressionOpLeft(Token leftOp, Expression expression) {
        this.leftOp = leftOp;
        this.expression = expression;
    }

    @Override
    public Token first() {
        return leftOp;
    }

    @Override
    public Token last() {
        return expression.last();
    }
}
