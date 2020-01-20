package canoe.ast.statement;

import canoe.lexis.Token;

/**
 * @author dawn
 */
public class StatementBreak implements Statement {

    private Token breakToken;

    public StatementBreak(Token breakToken) {
        this.breakToken = breakToken;
    }
}
