package canoe2.ast.expression;

import canoe2.ast.statement.Statements;
import canoe2.lexis.Kind;
import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class ExpressionLambdaStatements implements Expression {

    private Expression params;

    private Token lambda;

    private Token lb;

    private Statements statements;

    private Token rb;

    public ExpressionLambdaStatements(Expression params, Token lambda, Token lb, Statements statements, Token rb) {
        this.params = params;
        this.lambda = lambda;
        this.lb = lb;
        this.statements = statements;
        this.rb = rb;
    }

    @Override
    public boolean endWith(Kind kind) {
        return rb.getKind() == kind;
    }
}
