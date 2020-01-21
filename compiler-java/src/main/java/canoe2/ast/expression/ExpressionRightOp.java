package canoe2.ast.expression;

import canoe2.lexis.Kind;
import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class ExpressionRightOp implements Expression {

    private Expression expression;

    private Token rightOp;

    public ExpressionRightOp(Expression expression, Token rightOp) {
        this.expression = expression;
        this.rightOp = rightOp;
    }

    @Override
    public boolean endWith(Kind kind) {
        return rightOp.getKind() == kind;
    }
}
