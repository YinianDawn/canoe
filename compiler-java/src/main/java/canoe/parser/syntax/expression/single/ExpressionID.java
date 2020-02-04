package canoe.parser.syntax.expression.single;


import canoe.lexer.Token;
import canoe.parser.syntax.expression.Expression;

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

    @Override
    public String toString() {
        return ID.value();
    }
}
