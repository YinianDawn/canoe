package canoe.ast.statement;


import canoe.ast.expression.Expression;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class StatementReturn implements Statement {

    private Token returnToken;
    private Expression expression;

    public StatementReturn(Token returnToken, Expression expression) {
        this.returnToken = returnToken;
        this.expression = expression;
    }

    @Override
    public Token first() {
        return returnToken;
    }

    @Override
    public Token last() {
        return expression.last();
    }
}
