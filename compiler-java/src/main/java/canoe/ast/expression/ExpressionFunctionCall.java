package canoe.ast.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionFunctionCall implements Expression {

    private Expression expressionId;

    private ExpressionRoundBracket roundBracket;

    public ExpressionFunctionCall(Expression expressionId, ExpressionRoundBracket roundBracket) {
        this.expressionId = expressionId;
        this.roundBracket = roundBracket;
    }


    @Override
    public Token first() {
        return expressionId.first();
    }

    @Override
    public Token last() {
        return roundBracket.last();
    }
}
