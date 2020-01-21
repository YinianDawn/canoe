package canoe.ast.expression;

import canoe.lexis.Kind;

/**
 * @author dawn
 */
public class ExpressionFunction implements Expression {

    private Expression expression;

    private ExpressionRoundBracket roundBracket;

    public ExpressionFunction(Expression expression, ExpressionRoundBracket roundBracket) {
        this.expression = expression;
        this.roundBracket = roundBracket;
    }

    @Override
    public boolean endWith(Kind kind) {
        return roundBracket.endWith(kind);
    }
}
