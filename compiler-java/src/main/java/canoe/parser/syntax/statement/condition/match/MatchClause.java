package canoe.ast.statement.condition.match;


import canoe.ast.expression.Expression;
import canoe.ast.statement.Statements;
import canoe.lexer.Token;

import java.util.List;

/**
 * @author dawn
 */
public class MatchClause {

    private Token op;
    private Expression expression;
    private List<MatchOpExpression> others;

    private Token colon;

    private Token lb;

    private Statements statements;

    private Token rb;

    public MatchClause(Token op, Expression expression, List<MatchOpExpression> others, Token colon, Token lb, Statements statements, Token rb) {
        this.op = op;
        this.expression = expression;
        this.others = others;
        this.colon = colon;
        this.lb = lb;
        this.statements = statements;
        this.rb = rb;
    }
}
