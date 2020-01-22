package canoe.ast.statement.loop;

import canoe.ast.statement.StatementAssign;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class StatementLoopAssign {

    private Token comma;

    private StatementAssign statementAssign;


    public StatementLoopAssign(Token comma, StatementAssign statementAssign) {
        this.comma = comma;
        this.statementAssign = statementAssign;
    }
}
