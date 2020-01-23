package canoe.ast.statement;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class StatementGoto implements Statement {

    private Token gotoToken;

    private Token label;

    public StatementGoto(Token gotoToken, Token label) {
        this.gotoToken = gotoToken;
        this.label = label;
    }

    @Override
    public Token first() {
        return gotoToken;
    }

    @Override
    public Token last() {
        return label;
    }
}
