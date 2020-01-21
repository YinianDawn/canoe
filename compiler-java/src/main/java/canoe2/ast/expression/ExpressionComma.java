package canoe2.ast.expression;

import canoe2.lexis.Kind;
import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class ExpressionComma implements Expression {

    private Expression first;

    private Token comma;

    private Expression end;

    public ExpressionComma(Expression first, Token comma, Expression end) {
        this.first = first;
        this.comma = comma;
        this.end = end;
    }

    @Override
    public boolean endWith(Kind kind) {
        return null != comma ? first.endWith(kind) : end.endWith(kind);
    }
}
