package canoe.ast.statement.desc;

import canoe.ast.expression.Expression;
import canoe.lexis.Token;

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
