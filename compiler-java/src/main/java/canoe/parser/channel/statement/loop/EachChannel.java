package canoe.parser.channel.statement.loop;

import canoe.ast.expression.Expression;
import canoe.ast.expression.ExpressionID;
import canoe.ast.statement.Statements;
import canoe.ast.statement.loop.StatementEach;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.channel.expression.ExpressionChannel;

/**
 * @author dawn
 */
public class EachChannel extends Channel<StatementEach> {

    private Token each;
    private Token colon;
    private Token mark;
    private Expression item;
    private Token comma;
    private ExpressionID count;
    private Token in;
    private Expression iteratorExpression;
    private Token lb;
    private Statements statements;
    private Token rb;

    private EachChannel(Channel channel, Kind... end) {
        super(channel, end);
        if (glance().not(Kind.EACH)) {
            panic("must be each.", glance());
        }
        init();
    }

    @Override
    protected void init() {
        each = next();
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
        item = ExpressionChannel.produce(this, Kind.COMMA, Kind.IN);
        removeSpace();
        comma = glance();
        if (comma.is(Kind.COMMA)) {
            comma = next();
            if (comma.not(Kind.COMMA)) {
                panic("must be ,", comma);
            }
            removeSpace();
            Token id = next();
            if (id.not(Kind.ID)) {
                panic("must be ID", id);
            }
            count = new ExpressionID(id);
        } else {
            comma = null;
        }
        removeSpace();
        in = next();
        if (in.not(Kind.IN)) {
            panic("must be in", in);
        }
        removeSpace();
        iteratorExpression = ExpressionChannel.produce(this, Kind.LB);
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
        data = new StatementEach(each, colon, mark, item, comma, count, in, iteratorExpression, lb, statements, rb);
    }


    public static StatementEach produce(Channel channel, Kind... end) {
        return new EachChannel(channel, end).produce();
    }

}
