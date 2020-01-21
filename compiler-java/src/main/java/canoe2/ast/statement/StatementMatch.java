package canoe2.ast.statement;

import canoe2.ast.expression.Expression;
import canoe2.lexis.Token;

import java.util.List;

/**
 * @author dawn
 */
public class StatementMatch implements Statement {

    private Token match;

    private Token with;

    private Expression expression;

    private Token lb;

    private List<StatementMatchClause> clauses;

    private StatementMatchClause elseClause;

    private Token rb;

    public StatementMatch(Token match, Token with, Expression expression, Token lb, List<StatementMatchClause> clauses, StatementMatchClause elseClause, Token rb) {
        this.match = match;
        this.with = with;
        this.expression = expression;
        this.lb = lb;
        this.clauses = clauses;
        this.elseClause = elseClause;
        this.rb = rb;
    }
}
