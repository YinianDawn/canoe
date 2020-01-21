package canoe2.ast.expression;

import canoe2.lexis.Kind;
import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class ExpressionString implements Expression {

    private Token string;

    public ExpressionString(Token string) {
        this.string = string;
    }

    @Override
    public boolean endWith(Kind kind) {
        return string.getKind() == kind;
    }
}
