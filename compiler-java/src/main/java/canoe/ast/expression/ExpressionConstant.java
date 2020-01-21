package canoe.ast.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionConstant implements Expression {
    private Token constant;

    public ExpressionConstant(Token constant) {
        this.constant = constant;
    }
}
