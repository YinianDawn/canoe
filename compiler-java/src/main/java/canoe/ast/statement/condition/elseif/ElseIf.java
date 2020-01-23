package canoe.ast.statement.condition.elseif;

import canoe.ast.expression.Expression;
import canoe.ast.statement.Statements;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ElseIf {

    private Token elseIf;

    private Expression expression;

    private Token lb;

    private Statements elseIfStatements;

    private Token rb;

    public ElseIf(Token elseIf, Expression expression, Token lb, Statements elseIfStatements, Token rb) {
        this.elseIf = elseIf;
        this.expression = expression;
        this.lb = lb;
        this.elseIfStatements = elseIfStatements;
        this.rb = rb;
    }

    public Token last() {
        return rb;
    }
}
