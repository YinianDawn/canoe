package canoe.ast.expression;

import canoe.lexis.Kind;
import canoe.lexis.Token;

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
