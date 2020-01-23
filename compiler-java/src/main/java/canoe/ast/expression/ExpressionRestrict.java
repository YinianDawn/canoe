package canoe.ast.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionRestrict implements Expression {

    private Token colon;

    private Expression expression;

    public ExpressionRestrict(Token colon, Expression expression) {
        this.colon = colon;
        this.expression = expression;
    }

    @Override
    public Token first() {
        return colon;
    }

    @Override
    public Token last() {
        return expression.last();
    }
}
