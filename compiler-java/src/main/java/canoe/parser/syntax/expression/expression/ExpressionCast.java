package canoe.parser.syntax.expression.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionCast implements Expression {

    private Expression expressionID;
    private Token dot;
    private ExpressionRoundBracket roundBracket;

    public ExpressionCast(Expression expressionID, Token dot, ExpressionRoundBracket roundBracket) {
        this.expressionID = expressionID;
        this.dot = dot;
        this.roundBracket = roundBracket;
    }

    @Override
    public Token first() {
        return expressionID.first();
    }

    @Override
    public Token last() {
        return roundBracket.last();
    }
}
