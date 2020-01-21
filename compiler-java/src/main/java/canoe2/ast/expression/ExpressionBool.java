package canoe2.ast.expression;

import canoe2.lexis.Kind;
import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class ExpressionBool implements Expression {

    private Token bool;

    public ExpressionBool(Token bool) {
        this.bool = bool;
    }

    @Override
    public boolean endWith(Kind kind) {
        return bool.getKind() == kind;
    }
}
