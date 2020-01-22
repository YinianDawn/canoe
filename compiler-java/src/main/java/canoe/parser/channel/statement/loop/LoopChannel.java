package canoe.parser.channel.statement.loop;

import canoe.ast.statement.Statement;
import canoe.ast.statement.StatementAssign;
import canoe.ast.statement.Statements;
import canoe.ast.statement.loop.StatementLoop;
import canoe.ast.statement.loop.StatementLoopAssign;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.channel.statement.StatementChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dawn
 */
public class LoopChannel extends Channel<StatementLoop> {

    private Token loop;
    private Token colon;
    private Token mark;
    private StatementAssign assign;
    private List<StatementLoopAssign> loopAssigns = new ArrayList<>();
    private Token lb;
    private Statements statements;
    private Token rb;

    private LoopChannel(Channel channel, Kind... end) {
        super(channel, end);
        if (glance().not(Kind.LOOP)) {
            panic("must be loop.", glance());
        }
        init();
    }

    @Override
    protected void init() {
        loop = next();
        removeSpace();
        colon = glance();
        if (colon.isColon()) {
            colon = next();
            mark = next();
            if (mark.not(Kind.ID)) {
                panic("must be ID", mark);
            }
            if (!colon.next(mark)) {
                panic(mark.kind.getSign() + " must follow sign : , no space", mark);
            }
        } else {
            colon = null;
        }
        removeSpace();
        // 可能存在多个赋值表达式
        Token next = glance();
        if (next.not(Kind.LB)) {
            assign = parseStatementAssign(
                    StatementChannel.produce(this, Kind.COMMA, Kind.LB));
            Token comma;
            StatementAssign statementAssign;
            removeSpace();
            next = glance();
            while (next.is(Kind.COMMA)) {
                comma = next();
                statementAssign = parseStatementAssign(
                        StatementChannel.produce(this, Kind.COMMA, Kind.LB));
                loopAssigns.add(new StatementLoopAssign(comma, statementAssign));
                removeSpace();
                next = glance();
            }
        }
        lb = next();
        if (lb.not(Kind.LB)) {
            panic("must be { .", lb);
        }
        removeSpaceOrCR();
        statements = parseStatements(Kind.RB, Kind.CR);
        removeSpaceOrCR();
        rb = next();
        if (rb.not(Kind.RB)) {
            panic("must be } .", rb);
        }
        data = new StatementLoop(loop, colon, mark, assign, loopAssigns, lb, statements, rb);
    }

    private StatementAssign parseStatementAssign(Statement statement) {
        if (statement instanceof  StatementAssign) {
            return (StatementAssign) statement;
        }
        panic("should be assign statement.");
        return null;
    }



    public static StatementLoop produce(Channel channel, Kind... end) {
        return new LoopChannel(channel, end).produce();
    }

}
