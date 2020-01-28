package canoe.ast.statement.condition.match;


import canoe.ast.statement.Statements;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class MatchElseClause {

    private Token elseToken;

    private Token colon;

    private Token lb;

    private Statements statements;

    private Token rb;

    public MatchElseClause(Token elseToken, Token colon, Token lb, Statements statements, Token rb) {
        this.elseToken = elseToken;
        this.colon = colon;
        this.lb = lb;
        this.statements = statements;
        this.rb = rb;
    }
}
