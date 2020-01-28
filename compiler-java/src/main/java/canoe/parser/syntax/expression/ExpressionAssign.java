package canoe.parser.syntax.expression;


import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionAssign implements Expression {

    private final Expression id;

    private final Token assign;

    private final Expression expression;

    public ExpressionAssign(Expression id, Token assign, Expression expression) {
        this.id = id;
        this.assign = assign;
        this.expression = expression;
    }

    @Override
    public Token first() {
        return id.first();
    }

    @Override
    public Token last() {
        return expression.last();
    }
}
