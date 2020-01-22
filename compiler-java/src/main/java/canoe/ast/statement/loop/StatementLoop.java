package canoe.ast.statement.loop;

import canoe.ast.statement.Statement;
import canoe.ast.statement.StatementAssign;
import canoe.ast.statement.Statements;
import canoe.lexer.Token;

import java.util.List;

/**
 * @author dawn
 */
public class StatementLoop implements Statement {

    private Token loop;

    private Token colon;
    private Token mark;

    private StatementAssign assign;
    private List<StatementLoopAssign> loopAssigns;

    private Token lb;
    private Statements statements;
    private Token rb;

    public StatementLoop(Token loop, Token colon, Token mark, StatementAssign assign, List<StatementLoopAssign> loopAssigns, Token lb, Statements statements, Token rb) {
        this.loop = loop;
        this.colon = colon;
        this.mark = mark;
        this.assign = assign;
        this.loopAssigns = loopAssigns;
        this.lb = lb;
        this.statements = statements;
        this.rb = rb;
    }
}
