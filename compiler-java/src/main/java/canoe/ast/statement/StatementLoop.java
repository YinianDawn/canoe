package canoe.ast.statement;

import canoe.lexis.Token;

/**
 * @author dawn
 */
public class StatementLoop implements Statement {

    private Token loop;

    private Token colon;

    private Token id;

    private Token lb;

    private Statements statements;

    private Token rb;

    public StatementLoop(Token loop, Token colon, Token id, Token lb, Statements statements, Token rb) {
        this.loop = loop;
        this.colon = colon;
        this.id = id;
        this.lb = lb;
        this.statements = statements;
        this.rb = rb;
    }
}
