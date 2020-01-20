package canoe.ast.expression;

import canoe.lexis.Kind;
import canoe.lexis.Token;

/**
 * @author dawn
 */
public class ExpressionParams implements Expression {

    private Token lr;

    private Expression expression;

    private Token rr;

    public ExpressionParams(Token lr, Expression expression, Token rr) {
        this.lr = lr;
        this.expression = expression;
        this.rr = rr;
    }

    @Override
    public boolean endWith(Kind kind) {
        return null != rr ? rr.getKind() == kind : expression.endWith(kind);
    }
}
