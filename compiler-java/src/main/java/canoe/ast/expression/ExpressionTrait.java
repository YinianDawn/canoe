package canoe.ast.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionTrait {

    private Token colon;

    private Expression expression;

    public ExpressionTrait(Token colon, Expression expression) {
        this.colon = colon;
        this.expression = expression;
    }

    public Token first() {
        return colon;
    }

    public Token last() {
        return expression.last();
    }

}
