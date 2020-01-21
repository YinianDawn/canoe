package canoe.ast.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionOpRight implements Expression {

    private Expression expression;
    private Token rightOp;


    public ExpressionOpRight(Expression expression, Token rightOp) {
        this.expression = expression;
        this.rightOp = rightOp;
    }
}
