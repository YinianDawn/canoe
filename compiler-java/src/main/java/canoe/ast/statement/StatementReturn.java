package canoe.ast.statement;

import canoe.ast.object.Object;
import canoe.lexis.Token;

/**
 * @author dawn
 */
public class StatementReturn extends Statement {
    private Token returnToken;
    private Object object;

    public StatementReturn(Token returnToken, Object object) {
        this.returnToken = returnToken;
        this.object = object;
    }
}
