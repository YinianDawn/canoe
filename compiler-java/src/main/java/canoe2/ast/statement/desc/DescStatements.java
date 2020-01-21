package canoe.ast.statement.desc;

import canoe.ast.statement.Statements;
import canoe.lexis.Token;

/**
 * @author dawn
 */
public class DescStatements implements Desc {

    private Token colon;

    private Token lb;

    private Statements statements;

    private Token rb;

    public DescStatements(Token colon, Token lb, Statements statements, Token rb) {
        this.colon = colon;
        this.lb = lb;
        this.statements = statements;
        this.rb = rb;
    }
}
