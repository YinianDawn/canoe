package canoe2.ast.statement;

import canoe2.ast.expression.Expression;
import canoe2.ast.expression.ExpressionComma;
import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class StatementFor implements Statement {

    private Token forToken;

    private Token colon;

    private Token id;

    private ExpressionComma variableExpression;

    private Token in;

    private Expression iteratorExpression;

    private Token lb;

    private Statements statements;

    private Token rb;

    public StatementFor(Token forToken, Token colon, Token id, ExpressionComma variableExpression, Token in, Expression iteratorExpression, Token lb, Statements statements, Token rb) {
        this.forToken = forToken;
        this.colon = colon;
        this.id = id;
        this.variableExpression = variableExpression;
        this.in = in;
        this.iteratorExpression = iteratorExpression;
        this.lb = lb;
        this.statements = statements;
        this.rb = rb;
    }
}
