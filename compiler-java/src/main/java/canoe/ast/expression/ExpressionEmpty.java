package canoe.ast.expression;

import canoe.lexis.Kind;

/**
 * @author dawn
 */
public class ExpressionEmpty implements Expression {

    @Override
    public boolean endWith(Kind kind) {
        return false;
    }
}
