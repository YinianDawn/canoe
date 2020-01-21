package canoe.parser.channel.statement;

import canoe.ast.expression.Expression;
import canoe.ast.statement.Statement;
import canoe.ast.statement.StatementEmpty;
import canoe.ast.statement.StatementIf;
import canoe.ast.statement.Statements;
import canoe.ast.statement.elseif.ElseIf;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.TokenStream;
import canoe.parser.channel.Channel;
import canoe.parser.channel.expression.ExpressionChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dawn
 */
public class IfChannel extends Channel {

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

    private StatementIf statementIf;

    public IfChannel(String name, TokenStream stream) {
        super(name, stream);
        if (stream.glance().not(Kind.IF)) {
            panic("must be if.", stream.glance());
        }
        parse();
    }

    public StatementIf get() {
        return statementIf;
    }

    private void parse() {
        ifToken = next();
        parseBlock(true);
        expression = tempExpression;
        lb = tempLB;
        thenStatements = tempStatements;
        rb = tempRB;

        removeSpace();
        Token elseIFToken = glance();
        while (elseIFToken.is(Kind.ELSE_IF)) {
            elseIFToken = next();
            parseBlock(true);
            elseIfs.add(new ElseIf(elseIFToken, tempExpression, tempLB, tempStatements, tempRB));
            removeSpace();
            elseIFToken = glance();
        }

        if (elseIFToken.is(Kind.ELSE)) {
            elseToken = next();
            parseBlock(false);

            elseLB = tempLB;
            elseStatements = tempStatements;
            elseRB = tempRB;
        }
        statementIf = new StatementIf(ifToken, expression, lb, thenStatements, rb,
                elseIfs, elseToken, elseLB, elseStatements, elseRB);
    }

    private Expression tempExpression;
    private Token tempLB;
    private Statements tempStatements;
    private Token tempRB;
    private void parseBlock(boolean expression) {
        tempExpression = null;
        tempLB = null;
        tempStatements = null;
        tempRB = null;

        if (expression) {
            removeSpace();
            tempExpression = new ExpressionChannel(this, Kind.LB).get();
            if (null == tempExpression) {
                panic("if expression can not be null");
            }
        }
        removeSpaceOrCR();
        tempLB = next();
        if (tempLB.not(Kind.LB)) {
            panic("statement must start with { .", tempLB);
        }
        removeSpaceOrCR();
        tempStatements = parseStatements();
        removeSpaceOrCR();
        tempRB = next();
        if (tempRB.not(Kind.RB)) {
            panic("statement must end with } .", tempRB);
        }
    }

    private Statements parseStatements() {
        List<Statement> statements = new ArrayList<>();
        // 解析多个语句
        removeSpaceOrCR();
        Statement statement = new StatementChannel(this, Kind.RB, Kind.CR).get();
        while (!(statement instanceof StatementEmpty)) {
            statements.add(statement);
            removeSpaceOrCR();
            statement = new StatementChannel(this, Kind.RB, Kind.CR).get();
        }
        return new Statements(statements);
    }

}
