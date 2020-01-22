package canoe.ast.statement.condition;


import canoe.ast.expression.Expression;
import canoe.ast.statement.Statement;
import canoe.ast.statement.condition.match.MatchClause;
import canoe.ast.statement.condition.match.MatchElseClause;
import canoe.lexer.Token;

import java.util.List;

/**
 * @author dawn
 */
public class StatementMatch implements Statement {

    private Token matchToken;

    private Token colonToken;
    private Token withToken;

    private Expression expression;

    private Token lb;

    private List<MatchClause> clauses;

    private MatchElseClause elseClause;

    private Token rb;

    public StatementMatch(Token matchToken, Token colonToken, Token withToken, Expression expression, Token lb, List<MatchClause> clauses, MatchElseClause elseClause, Token rb) {
        this.matchToken = matchToken;
        this.colonToken = colonToken;
        this.withToken = withToken;
        this.expression = expression;
        this.lb = lb;
        this.clauses = clauses;
        this.elseClause = elseClause;
        this.rb = rb;
    }

}
