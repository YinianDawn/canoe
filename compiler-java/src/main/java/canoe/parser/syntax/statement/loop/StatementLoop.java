package canoe.parser.syntax.statement.loop;

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
    private Token label;

    private StatementAssign assign;
    private List<LoopAssign> loopAssigns;

    private Token lb;
    private Statements statements;
    private Token rb;

    public StatementLoop(Token loop, Token colon, Token label, StatementAssign assign, List<LoopAssign> loopAssigns, Token lb, Statements statements, Token rb) {
        this.loop = loop;
        this.colon = colon;
        this.label = label;
        this.assign = assign;
        this.loopAssigns = loopAssigns;
        this.lb = lb;
        this.statements = statements;
        this.rb = rb;
    }

    @Override
    public Token first() {
        return loop;
    }

    @Override
    public Token last() {
        return rb;
    }
}
