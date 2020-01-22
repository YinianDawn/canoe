package canoe.ast.expression;


import canoe.ast.statement.condition.StatementIf;

/**
 * @author dawn
 */
public class ExpressionIf implements Expression {

    private StatementIf statementIf;

    public ExpressionIf(StatementIf statementIf) {
        this.statementIf = statementIf;
    }
}
