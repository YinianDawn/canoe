package canoe.parser.syntax.expression.expression;

import canoe.ast.statement.Statement;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionComma {

    private Token comma;

    private Statement expression;

    public ExpressionComma(Token comma, Statement expression) {
        this.comma = comma;
        this.expression = expression;
    }

    public Token last() {
        return expression.last();
    }
}
