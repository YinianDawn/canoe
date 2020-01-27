package canoe.parser.syntax.expression;


import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionID implements Expression {

    private final Token ID;

    public ExpressionID(Token id) {
        this.ID = id;
    }

    @Override
    public Token first() {
        return ID;
    }

    @Override
    public Token last() {
        return ID;
    }
}
