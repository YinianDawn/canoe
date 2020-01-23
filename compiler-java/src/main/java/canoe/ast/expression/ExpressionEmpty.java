package canoe.ast.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionEmpty implements Expression {

    private Token left;
    private Token right;

    public ExpressionEmpty(Token left, Token right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Token first() {
        return right;
    }

    @Override
    public Token last() {
        return left;
    }
}
