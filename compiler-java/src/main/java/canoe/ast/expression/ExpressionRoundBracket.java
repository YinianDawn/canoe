package canoe.ast.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionRoundBracket implements Expression {

    private Token lr;

    private Expression expression;

    private Token rr;

    public ExpressionRoundBracket(Token lr, Expression expression, Token rr) {
        this.lr = lr;
        this.expression = expression;
        this.rr = rr;
    }

    @Override
    public Token first() {
        return lr;
    }

    @Override
    public Token last() {
        return rr;
    }
}
