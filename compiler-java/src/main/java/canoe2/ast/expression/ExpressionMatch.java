package canoe.ast.expression;

import canoe.ast.statement.StatementMatch;
import canoe.lexis.Kind;

/**
 * @author dawn
 */
public class ExpressionMatch implements Expression {

    private StatementMatch statementMatch;

    public ExpressionMatch(StatementMatch statementMatch) {
        this.statementMatch = statementMatch;
    }

    @Override
    public boolean endWith(Kind kind) {
        return Kind.RB == kind;
    }
}
