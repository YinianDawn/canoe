package canoe.ast.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionSquareBracket implements Expression {

    private Token ls;

    private Expression expression;

    private Token rs;

    public ExpressionSquareBracket(Token ls, Expression expression, Token rs) {
        this.ls = ls;
        this.expression = expression;
        this.rs = rs;
    }

    @Override
    public Token first() {
        return ls;
    }

    @Override
    public Token last() {
        return rs;
    }
}
