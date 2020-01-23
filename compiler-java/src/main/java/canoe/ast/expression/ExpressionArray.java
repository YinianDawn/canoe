package canoe.ast.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionArray implements Expression {

    private ExpressionSquareBracket squareBracket;
    private ExpressionStruct struct;

    public ExpressionArray(ExpressionSquareBracket squareBracket, ExpressionStruct struct) {
        this.squareBracket = squareBracket;
        this.struct = struct;
    }

    @Override
    public Token first() {
        return squareBracket.first();
    }

    @Override
    public Token last() {
        return struct.last();
    }
}
