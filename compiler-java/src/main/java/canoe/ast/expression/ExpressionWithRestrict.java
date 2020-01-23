package canoe.ast.expression;


import canoe.lexer.Token;

import java.util.List;

/**
 * @author dawn
 */
public class ExpressionWithRestrict implements Expression {

    private Expression expression;

    private List<ExpressionRestrict> restricts;

    public ExpressionWithRestrict(Expression expression, List<ExpressionRestrict> restricts) {
        this.expression = expression;
        this.restricts = restricts;
    }


    @Override
    public Token first() {
        return expression.first();
    }

    @Override
    public Token last() {
        return restricts.isEmpty() ? expression.last() : restricts.get(restricts.size() - 1).last();
    }
}
