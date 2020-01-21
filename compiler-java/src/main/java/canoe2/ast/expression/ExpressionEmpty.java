package canoe2.ast.expression;

import canoe2.lexis.Kind;

/**
 * @author dawn
 */
public class ExpressionEmpty implements Expression {

    @Override
    public boolean endWith(Kind kind) {
        return false;
    }
}
