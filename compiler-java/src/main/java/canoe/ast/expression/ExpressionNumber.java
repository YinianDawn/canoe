package canoe.ast.expression;

import canoe.lexis.Kind;
import canoe.lexis.Token;

/**
 * @author dawn
 */
public class ExpressionNumber implements Expression {

    private Token number;

    public ExpressionNumber(Token number) {
        this.number = number;
    }

    @Override
    public boolean endWith(Kind kind) {
        return number.getKind() == kind;
    }
}
