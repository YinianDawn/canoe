package canoe.parser.channel.statement.loop;

import canoe.ast.expression.Expression;
import canoe.ast.statement.Statement;
import canoe.ast.statement.StatementAssign;
import canoe.ast.statement.Statements;
import canoe.ast.statement.loop.StatementFor;
import canoe.ast.statement.loop.LoopAssign;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.channel.expression.ExpressionChannel;
import canoe.parser.channel.statement.StatementChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dawn
 */
public class ForChannel extends Channel<StatementFor> {

    private Token forToken;

    private Token colon;
    private Token mark;

    private StatementAssign assign;
    private List<LoopAssign> forAssigns = new ArrayList<>();
    private Token semi1;
    private Expression expression;
    private Token semi2;
    private Statement statement;

    private Token lb;
    private Statements statements;
    private Token rb;

    private ForChannel(Channel channel, Kind... end) {
        super(channel, end);
        if (glance().not(Kind.FOR)) {
            panic("must be for.", glance());
        }
        init();
    }

    @Override
    protected void init() {
        forToken = next();
        removeSpace();
        colon = glance();
        if (colon.isColon()) {
            colon = next();
            mark = next();
            if (mark.not(Kind.ID)) {
                panic("must be ID", mark);
            }
            if (!colon.next(mark)) {
                panic(mark.kind.sign + " must follow sign : , no space", mark);
            }
        } else {
            colon = null;
        }
        removeSpace();
        // 可能存在多个赋值表达式
        Token next = glance();
        if (next.not(Kind.LB)) {
            assign = parseStatementAssign(
                    StatementChannel.produce(this, Kind.COMMA, Kind.SEMI));
            Token comma;
            StatementAssign statementAssign;
            removeSpace();
            next = glance();
            while (next.is(Kind.COMMA)) {
                comma = next();
                statementAssign = parseStatementAssign(
                        StatementChannel.produce(this, Kind.COMMA, Kind.SEMI));
                forAssigns.add(new LoopAssign(comma, statementAssign));
                removeSpace();
                next = glance();
            }
        }
        semi1 = next();
        if (semi1.not(Kind.SEMI)) {
            panic("must be ; .", semi1);
        }
        removeSpace();
        expression = ExpressionChannel.produce(this, Kind.SEMI);
        removeSpace();
        semi2 = next();
        if (semi2.not(Kind.SEMI)) {
            panic("must be ; .", semi2);
        }
        statement = StatementChannel.produce(this, Kind.LB);
        removeSpace();
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
        data = new StatementFor(forToken, colon, mark,
                assign, forAssigns, semi1, expression, semi2, statement,
                lb, statements, rb);
    }

    private StatementAssign parseStatementAssign(Statement statement) {
        if (statement instanceof  StatementAssign) {
            return (StatementAssign) statement;
        }
        panic("should be assign statement.");
        return null;
    }

    public static StatementFor produce(Channel channel, Kind... end) {
        return new ForChannel(channel, end).produce();
    }

}
