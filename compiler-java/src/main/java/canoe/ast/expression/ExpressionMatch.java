package canoe.ast.expression;


import canoe.ast.statement.StatementMatch;

/**
 * @author dawn
 */
public class ExpressionMatch implements Expression {

    private StatementMatch statementMatch;

    public ExpressionMatch(StatementMatch statementMatch) {
        this.statementMatch = statementMatch;
    }
}
