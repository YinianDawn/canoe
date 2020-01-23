package canoe.ast.statement.loop;

import canoe.ast.statement.StatementAssign;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class LoopAssign {

    private Token comma;

    private StatementAssign statementAssign;

    public LoopAssign(Token comma, StatementAssign statementAssign) {
        this.comma = comma;
        this.statementAssign = statementAssign;
    }
}
