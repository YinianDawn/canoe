package canoe.parser.syntax.expression;


import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionKeyWord implements Expression {

    private final Token KEY_WORD;

    public ExpressionKeyWord(Token key) {
        this.KEY_WORD = key;
    }

    @Override
    public Token first() {
        return KEY_WORD;
    }

    @Override
    public Token last() {
        return KEY_WORD;
    }
}
