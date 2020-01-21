package canoe2.ast.expression;

import canoe2.lexis.Kind;
import canoe2.lexis.Token;

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
