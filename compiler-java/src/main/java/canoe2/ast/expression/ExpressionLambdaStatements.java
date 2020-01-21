package canoe.ast.expression;

import canoe.ast.statement.Statements;
import canoe.lexis.Kind;
import canoe.lexis.Token;

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
