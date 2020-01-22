package canoe.ast.expression;

/**
 * @author dawn
 */
public class ExpressionFunction implements Expression {

    private Expression expressionId;

    private ExpressionRoundBracket roundBracket;

    public ExpressionFunction(Expression expressionId, ExpressionRoundBracket roundBracket) {
        this.expressionId = expressionId;
        this.roundBracket = roundBracket;
    }

}
