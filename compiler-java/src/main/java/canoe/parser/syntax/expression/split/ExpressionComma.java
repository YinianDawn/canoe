package canoe.parser.syntax.expression.split;

import canoe.lexer.Token;
import canoe.parser.syntax.expression.Expression;

/**
 * @author dawn
 */
public class ExpressionComma implements Expression {

    private final Expression left;
    private final Token COMMA;
    private final Expression right;


    public ExpressionComma(Expression left, Token comma, Expression right) {
        this.left = left;
        this.COMMA = comma;
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
