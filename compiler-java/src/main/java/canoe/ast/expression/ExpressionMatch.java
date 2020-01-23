package canoe.ast.expression;


import canoe.ast.statement.condition.StatementMatch;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionMatch implements Expression {

    private StatementMatch statementMatch;

    public ExpressionMatch(StatementMatch statementMatch) {
        this.statementMatch = statementMatch;
    }

    @Override
    public Token first() {
        return statementMatch.first();
    }

    @Override
    public Token last() {
        return statementMatch.last();
    }
}
