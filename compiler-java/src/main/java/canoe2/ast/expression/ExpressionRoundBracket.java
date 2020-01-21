package canoe.ast.expression;

import canoe.lexis.Kind;
import canoe.lexis.Token;

/**
 * @author dawn
 */
public class ExpressionRoundBracket implements Expression {

    private Token lr;

    private Expression expression;

    private Token rr;

    public ExpressionRoundBracket(Token lr, Expression expression, Token rr) {
        this.lr = lr;
        this.expression = expression;
        this.rr = rr;
    }


    @Override
    public boolean endWith(Kind kind) {
        return rr.getKind() == kind;
    }
}
