package canoe.ast.expression;


import canoe.lexer.Token;

import java.util.List;

/**
 * @author dawn
 */
public class ExpressionWithTrait implements Expression {

    private Expression expression;

    private List<ExpressionTrait> traits;

    public ExpressionWithTrait(Expression expression, List<ExpressionTrait> traits) {
        this.expression = expression;
        this.traits = traits;
    }


    @Override
    public Token first() {
        return expression.first();
    }

    @Override
    public Token last() {
        return traits.isEmpty() ? expression.last() : traits.get(traits.size() - 1).last();
    }
}
