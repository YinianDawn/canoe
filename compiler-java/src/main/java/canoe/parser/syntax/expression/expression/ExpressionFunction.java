package canoe.parser.syntax.expression.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionFunction implements Expression {

    private ExpressionRoundBracket roundBracket;

    private ExpressionStruct struct;

    public ExpressionFunction(ExpressionRoundBracket roundBracket, ExpressionStruct struct) {
        this.roundBracket = roundBracket;
        this.struct = struct;
    }

    @Override
    public Token first() {
        return roundBracket.first();
    }

    @Override
    public Token last() {
        return struct.last();
    }
}
