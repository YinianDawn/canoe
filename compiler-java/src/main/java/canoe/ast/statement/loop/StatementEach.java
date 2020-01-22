package canoe.ast.statement.loop;

import canoe.ast.expression.Expression;
import canoe.ast.expression.ExpressionID;
import canoe.ast.statement.Statement;
import canoe.ast.statement.Statements;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class StatementEach implements Statement {

    private Token each;

    private Token colon;
    private Token mark;

    private Expression item;
    private Token comma;
    private ExpressionID count;

    private Token in;
    private Expression iteratorExpression;

    private Token lb;
    private Statements statements;
    private Token rb;

    public StatementEach(Token each, Token colon, Token mark, Expression item, Token comma, ExpressionID count, Token in, Expression iteratorExpression, Token lb, Statements statements, Token rb) {
        this.each = each;
        this.colon = colon;
        this.mark = mark;
        this.item = item;
        this.comma = comma;
        this.count = count;
        this.in = in;
        this.iteratorExpression = iteratorExpression;
        this.lb = lb;
        this.statements = statements;
        this.rb = rb;
    }
}
