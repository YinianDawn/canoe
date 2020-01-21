package canoe.ast.expression;

import canoe.lexis.Kind;
import canoe.lexis.Token;

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
