package canoe.ast.expression;


import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionKeyWord implements Expression {

    private Token key;

    public ExpressionKeyWord(Token key) {
        this.key = key;
    }

    @Override
    public Token first() {
        return key;
    }

    @Override
    public Token last() {
        return key;
    }
}
