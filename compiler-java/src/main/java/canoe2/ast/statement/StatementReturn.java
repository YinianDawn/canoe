package canoe2.ast.statement;

import canoe2.ast.object.Object;
import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class StatementReturn implements Statement {
    private Token returnToken;
    private Object object;

    public StatementReturn(Token returnToken, Object object) {
        this.returnToken = returnToken;
        this.object = object;
    }
}
