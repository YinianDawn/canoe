package canoe.ast.statement.condition;

import canoe.ast.statement.Statement;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class StatementWith implements Statement {

    private Token withToken;

    public StatementWith(Token withToken) {
        this.withToken = withToken;
    }

    @Override
    public Token first() {
        return withToken;
    }

    @Override
    public Token last() {
        return withToken;
    }
}
