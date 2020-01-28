package canoe.parser.syntax.expression;


import canoe.lexer.Token;
import canoe.parser.syntax.statement.condition.StatementIf;

/**
 * @author dawn
 */
public class ExpressionIf implements Expression {

    private StatementIf statement;

    public ExpressionIf(StatementIf statement) {
        this.statement = statement;
    }

    @Override
    public Token first() {
        return statement.first();
    }

    @Override
    public Token last() {
        return statement.last();
    }
}
