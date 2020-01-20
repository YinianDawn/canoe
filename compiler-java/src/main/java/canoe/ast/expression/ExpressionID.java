package canoe.ast.expression;

import canoe.lexis.Kind;
import canoe.lexis.Token;

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
