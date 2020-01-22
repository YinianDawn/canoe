package canoe.parser.channel.statement.condition;

import canoe.ast.expression.Expression;
import canoe.ast.statement.Statements;
import canoe.ast.statement.condition.StatementIf;
import canoe.ast.statement.condition.elseif.ElseIf;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.channel.expression.ExpressionChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dawn
 */
public class IfChannel extends Channel<StatementIf> {

    private Token ifToken;
    private Expression expression;
    private Token lb;
    private Statements thenStatements;
    private Token rb;
    private List<ElseIf> elseIfs = new ArrayList<>();
    private Token elseToken;
    private Token elseLB;
    private Statements elseStatements;
    private Token elseRB;

    private IfChannel(Channel channel, Kind... end) {
        super(channel, end);
        if (glance().not(Kind.IF)) {
            panic("must be if.", glance());
        }
        init();
    }

    @Override
    protected void init() {
        ifToken = next();
        Block b = parseBlock(true);
        expression = b.expression;
        lb = b.lb;
        thenStatements = b.statements;
        rb = b.rb;
        removeSpace();
        Token elseIFToken = glance();
        while (elseIFToken.is(Kind.ELSE_IF)) {
            elseIFToken = next();
            b = parseBlock(true);
            elseIfs.add(new ElseIf(elseIFToken, b.expression, b.lb, b.statements, b.rb));
            removeSpace();
            elseIFToken = glance();
        }
        if (elseIFToken.is(Kind.ELSE)) {
            elseToken = next();
            b = parseBlock(false);

            elseLB = b.lb;
            elseStatements = b.statements;
            elseRB = b.rb;
        }
        data = new StatementIf(ifToken, expression, lb, thenStatements, rb,
                elseIfs, elseToken, elseLB, elseStatements, elseRB);
    }


    private Block parseBlock(boolean expression) {
        Expression boolExpression = null;
        Token lb;
        Statements statements;
        Token rb;

        if (expression) {
            removeSpace();
            boolExpression = ExpressionChannel.produce(this, Kind.LB);
        }
        removeSpace();
        lb = next();
        if (lb.not(Kind.LB)) {
            panic("statement must start with { .", lb);
        }
        removeSpaceOrCR();
        statements = parseStatements(Kind.RB, Kind.CR);
        removeSpaceOrCR();
        rb = next();
        if (rb.not(Kind.RB)) {
            panic("statement must end with } .", rb);
        }
        return new Block(boolExpression, lb, statements, rb);
    }

    private static class Block {
        private Expression expression;
        private Token lb;
        private Statements statements;
        private Token rb;
        public Block(Expression expression, Token lb, Statements statements, Token rb) {
            this.expression = expression;
            this.lb = lb;
            this.statements = statements;
            this.rb = rb;
        }
    }

    public static StatementIf produce(Channel channel, Kind... end) {
        return new IfChannel(channel, end).produce();
    }

}
