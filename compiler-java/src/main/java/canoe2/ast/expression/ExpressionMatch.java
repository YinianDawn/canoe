package canoe2.ast.expression;

import canoe2.ast.statement.StatementMatch;
import canoe2.lexis.Kind;

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
