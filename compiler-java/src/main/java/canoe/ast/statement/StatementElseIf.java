package canoe.ast.statement;

import canoe.ast.expression.Expression;
import canoe.lexis.Token;

/**
 * @author dawn
 */
public class StatementElseIf implements Statement {

    private Token elseIf;

    private Expression expression;

    private Token lb;

    private Statements elseIfStatements;

    private Token rb;

    public StatementElseIf(Token elseIf, Expression expression, Token lb, Statements elseIfStatements, Token rb) {
        this.elseIf = elseIf;
        this.expression = expression;
        this.lb = lb;
        this.elseIfStatements = elseIfStatements;
        this.rb = rb;
    }
}
