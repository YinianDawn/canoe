package canoe2.ast.expression;

import canoe2.ast.statement.StatementIf;
import canoe2.lexis.Kind;

/**
 * @author dawn
 */
public class ExpressionIf implements Expression {

    private StatementIf statementIf;

    public ExpressionIf(StatementIf statementIf) {
        this.statementIf = statementIf;
    }

    @Override
    public boolean endWith(Kind kind) {
        return Kind.RB == kind;
    }
}
