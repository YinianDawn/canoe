package canoe.ast.expression;


import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionID implements Expression {

    private Token id;

    public ExpressionID(Token id) {
        this.id = id;
    }

    @Override
    public Token first() {
        return id;
    }

    @Override
    public Token last() {
        return id;
    }
}
