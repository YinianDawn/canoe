package canoe.ast.statement;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class StatementWith implements Statement {

    private Token withToken;

    public StatementWith(Token withToken) {
        this.withToken = withToken;
    }
}
