package canoe2.ast.expression;

import canoe2.lexis.Kind;
import canoe2.lexis.Token;

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
