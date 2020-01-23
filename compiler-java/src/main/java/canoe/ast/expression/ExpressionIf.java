package canoe.ast.expression;


import canoe.ast.statement.condition.StatementIf;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionIf implements Expression {

    private StatementIf statementIf;

    public ExpressionIf(StatementIf statementIf) {
        this.statementIf = statementIf;
    }

    @Override
    public Token first() {
        return statementIf.first();
    }

    @Override
    public Token last() {
        return statementIf.last();
    }
}
