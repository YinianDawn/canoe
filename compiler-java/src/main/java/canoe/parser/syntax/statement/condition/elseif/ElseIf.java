package canoe.parser.syntax.statement.condition.elseif;

import canoe.lexer.Token;
import canoe.parser.syntax.Statements;
import canoe.parser.syntax.expression.Expression;

/**
 * @author dawn
 */
public class ElseIf {

    private Token ELSE_IF;
    private Expression condition;
    private Token LB;
    private Statements statements;
    private Token RB;

    public ElseIf(Token symbol, Expression condition, Token lb, Statements statements, Token rb) {
        this.ELSE_IF = symbol;
        this.condition = condition;
        this.LB = lb;
        this.statements = statements;
        this.RB = rb;
    }

    public Token last() {
        return RB;
    }
}
