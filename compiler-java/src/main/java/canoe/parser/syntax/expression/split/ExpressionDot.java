package canoe.parser.syntax.expression.split;

import canoe.lexer.Token;
import canoe.parser.syntax.expression.Expression;

/**
 * @author dawn
 */
public class ExpressionDot implements Expression {

    private final Expression left;
    private final Token DOT;
    private final Expression right;


    public ExpressionDot(Expression left, Token dot, Expression right) {
        this.left = left;
        this.DOT = dot;
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

    @Override
    public String toString() {
        return left.toString() + DOT.kind.value + right.toString();
    }
}
