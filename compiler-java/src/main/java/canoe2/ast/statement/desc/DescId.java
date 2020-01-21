package canoe2.ast.statement.desc;

import canoe2.ast.expression.Expression;
import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class DescId implements Desc {

    private Token colon;

    private Expression id;

    public DescId(Token colon, Expression id) {
        this.colon = colon;
        this.id = id;
    }
}
