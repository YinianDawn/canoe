package canoe.ast.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionChangeID implements Expression {

    private ExpressionID expressionID;

    private Token dot3;

    public ExpressionChangeID(ExpressionID expressionID, Token dot3) {
        this.expressionID = expressionID;
        this.dot3 = dot3;
    }

    @Override
    public Token first() {
        return expressionID.first();
    }

    @Override
    public Token last() {
        return dot3;
    }
}
