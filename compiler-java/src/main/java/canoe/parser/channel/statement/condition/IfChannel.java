package canoe.parser.channel.statement.condition;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.channel.expression.ExpressionChannel;
import canoe.parser.syntax.Statements;
import canoe.parser.syntax.expression.Expression;
import canoe.parser.syntax.statement.condition.StatementIf;
import canoe.parser.syntax.statement.condition.elseif.ElseIf;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dawn
 */
public class IfChannel extends Channel<StatementIf> {

    private Token IF;
    private Expression condition;
    private Token LB;
    private Statements thenStatements;
    private Token RB;
    private List<ElseIf> elseIfs = new ArrayList<>();
    private Token ELSE;
    private Token ELSE_LB;
    private Statements elseStatements;
    private Token ELSE_RB;

    private IfChannel(Channel channel, Kind... end) {
        super(channel, end);
        if (glance().not(Kind.IF)) {
            panic("must be if", glance());
        }
        init();
    }

    @Override
    protected void init() {
        IF = next();
        Block b = parseBlock(true);
        condition = b.condition;
        LB = b.LB;
        thenStatements = b.statements;
        RB = b.RB;
        dropSpaces();
        Token elseIFToken = glance();
        while (elseIFToken.is(Kind.ELSE_IF)) {
            elseIFToken = next();
            b = parseBlock(true);
            elseIfs.add(new ElseIf(elseIFToken, b.condition, b.LB, b.statements, b.RB));
            dropSpaces();
            elseIFToken = glance();
        }
        if (elseIFToken.is(Kind.ELSE)) {
            ELSE = next();
            b = parseBlock(false);
            ELSE_LB = b.LB;
            elseStatements = b.statements;
            ELSE_RB = b.RB;
        }
        data = new StatementIf(IF, condition, LB, thenStatements, RB,
                elseIfs, ELSE, ELSE_LB, elseStatements, ELSE_RB);
    }


    private Block parseBlock(boolean condition) {
        Expression boolExpression = null;
        Token lb;
        Statements statements;
        Token rb;

        if (condition) {
            dropSpaces();
            boolExpression = ExpressionChannel.produce(this, Kind.LB);
        }
        dropSpaces();
        lb = next();
        if (lb.not(Kind.LB)) {
            panic("statement must start with {", lb);
        }
        dropSpacesCR();
        statements = parseStatements(Kind.RB, Kind.CR, Kind.SEMI);
        dropSpacesCR();
        rb = next();
        if (rb.not(Kind.RB)) {
            panic("statement must end with }", rb);
        }
        return new Block(boolExpression, lb, statements, rb);
    }

    private static class Block {
        private Expression condition;
        private Token LB;
        private Statements statements;
        private Token RB;
        Block(Expression condition, Token lb, Statements statements, Token rb) {
            this.condition = condition;
            this.LB = lb;
            this.statements = statements;
            this.RB = rb;
        }
    }

    public static StatementIf make(Channel channel, Kind... end) {
        return new IfChannel(channel, end).make();
    }

}
