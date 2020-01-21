package canoe2.ast.statement;

import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class StatementBreak implements Statement {

    private Token breakToken;

    public StatementBreak(Token breakToken) {
        this.breakToken = breakToken;
    }
}
