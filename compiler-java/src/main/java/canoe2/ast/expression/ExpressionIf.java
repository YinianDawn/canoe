package canoe.ast.expression;

import canoe.ast.statement.StatementIf;
import canoe.lexis.Kind;

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
