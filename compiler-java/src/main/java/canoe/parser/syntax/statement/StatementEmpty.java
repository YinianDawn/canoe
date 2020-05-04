package canoe.parser.syntax.statement;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class StatementEmpty implements Statement {

    private Token left;
    private Token right;

    public StatementEmpty(Token left, Token right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Token first() {
        return right;
    }

    @Override
    public Token last() {
        return left;
    }

    @Override
    public String getId() {
        return null;
    }
}
