package canoe.ast.statement.match;

import canoe.ast.expression.Expression;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class MatchOpExpression {

    private Token comma;
    private Token op;
    private Expression expression;

    public MatchOpExpression(Token comma, Token op, Expression expression) {
        this.comma = comma;
        this.op = op;
        this.expression = expression;
    }
}
