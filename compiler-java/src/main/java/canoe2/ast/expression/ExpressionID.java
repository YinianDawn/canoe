package canoe2.ast.expression;

import canoe2.lexis.Kind;
import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class ExpressionID implements Expression {

    private Token id;

    public ExpressionID(Token id) {
        this.id = id;
    }

    @Override
    public boolean endWith(Kind kind) {
        return id.getKind() == kind;
    }
}
