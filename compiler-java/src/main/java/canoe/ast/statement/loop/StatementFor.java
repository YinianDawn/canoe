package canoe.ast.statement.loop;

import canoe.ast.expression.Expression;
import canoe.ast.statement.Statement;
import canoe.ast.statement.StatementAssign;
import canoe.ast.statement.Statements;
import canoe.lexer.Token;

import java.util.List;

/**
 * @author dawn
 */
public class StatementFor implements Statement {

    private Token forToken;

    private Token colon;
    private Token mark;

    private StatementAssign assign;
    private List<StatementLoopAssign> forAssigns;
    private Token semi1;
    private Expression expression;
    private Token semi2;
    private Statement statement;

    private Token lb;
    private Statements statements;
    private Token rb;

    public StatementFor(Token forToken, Token colon, Token mark, StatementAssign assign, List<StatementLoopAssign> forAssigns, Token semi1, Expression expression, Token semi2, Statement statement, Token lb, Statements statements, Token rb) {
        this.forToken = forToken;
        this.colon = colon;
        this.mark = mark;
        this.assign = assign;
        this.forAssigns = forAssigns;
        this.semi1 = semi1;
        this.expression = expression;
        this.semi2 = semi2;
        this.statement = statement;
        this.lb = lb;
        this.statements = statements;
        this.rb = rb;
    }
}
