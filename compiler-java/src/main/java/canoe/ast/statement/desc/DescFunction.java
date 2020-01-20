package canoe.ast.statement.desc;

import canoe.ast.expression.Expression;
import canoe.lexis.Token;

/**
 * @author dawn
 */
public class DescFunction implements Desc {

    private Token colon;

    private Token ls;

    private Expression expression;

    private Token rs;

    public DescFunction(Token colon, Token ls, Expression expression, Token rs) {
        this.colon = colon;
        this.ls = ls;
        this.expression = expression;
        this.rs = rs;
    }
}
