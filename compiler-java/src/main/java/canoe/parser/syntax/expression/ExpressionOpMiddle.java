package canoe.parser.syntax.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionOpMiddle implements Expression {

    private final Expression left;
    private final Token op;
    private final Expression right;


    public ExpressionOpMiddle(Expression left, Token op, Expression right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public Token first() {
        return left.first();
    }

    @Override
    public Token last() {
        return right.last();
    }
}
