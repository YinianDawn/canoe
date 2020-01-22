package canoe.ast.expression;


import canoe.ast.statement.condition.StatementMatch;

/**
 * @author dawn
 */
public class ExpressionMatch implements Expression {

    private StatementMatch statementMatch;

    public ExpressionMatch(StatementMatch statementMatch) {
        this.statementMatch = statementMatch;
    }
}
