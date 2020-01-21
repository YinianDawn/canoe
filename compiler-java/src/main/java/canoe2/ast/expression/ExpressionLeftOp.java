package canoe.ast.expression;

import canoe.lexis.Kind;
import canoe.lexis.Token;

/**
 * @author dawn
 */
public class ExpressionLeftOp implements Expression {

    private Token leftOp;

    private Expression expression;

    public ExpressionLeftOp(Token leftOp, Expression expression) {
        this.leftOp = leftOp;
        this.expression = expression;
    }

    @Override
    public boolean endWith(Kind kind) {
        return expression.endWith(kind);
    }
}
