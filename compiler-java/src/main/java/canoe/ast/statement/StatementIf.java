package canoe.ast.statement;

import canoe.ast.expression.Expression;
import canoe.lexis.Token;

import java.util.List;

/**
 * @author dawn
 */
public class StatementIf implements Statement {

    private Token ifToken;

    private Expression expression;

    private Token lb;

    private Statements thenStatements;

    private Token rb;

    private List<StatementElseIf> elseIfs;

    private Token elseToken;

    private Token elseLb;

    private Statements elseStatements;

    private Token elseRb;

    public StatementIf(Token ifToken, Expression expression, Token lb, Statements thenStatements, Token rb, List<StatementElseIf> elseIfs, Token elseToken, Token elseLb, Statements elseStatements, Token elseRb) {
        this.ifToken = ifToken;
        this.expression = expression;
        this.lb = lb;
        this.thenStatements = thenStatements;
        this.rb = rb;
        this.elseIfs = elseIfs;
        this.elseToken = elseToken;
        this.elseLb = elseLb;
        this.elseStatements = elseStatements;
        this.elseRb = elseRb;
    }
}
