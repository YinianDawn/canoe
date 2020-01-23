package canoe.ast.expression;


import canoe.ast.statement.Statements;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class ExpressionStruct implements Expression {

    private Token lb;

    private Statements statements;

    private Token rb;

    public ExpressionStruct(Token lb, Statements statements, Token rb) {
        this.lb = lb;
        this.statements = statements;
        this.rb = rb;
    }

    @Override
    public Token first() {
        return lb;
    }

    @Override
    public Token last() {
        return rb;
    }
}
