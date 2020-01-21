package canoe.ast.statement;


import canoe.ast.expression.Expression;
import canoe.ast.statement.elseif.ElseIf;
import canoe.lexer.Token;

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

    private List<ElseIf> elseIfs;

    private Token elseToken;

    private Token elseLB;

    private Statements elseStatements;

    private Token elseRB;

    public StatementIf(Token ifToken, Expression expression, Token lb, Statements thenStatements, Token rb, List<ElseIf> elseIfs, Token elseToken, Token elseLB, Statements elseStatements, Token elseRB) {
        this.ifToken = ifToken;
        this.expression = expression;
        this.lb = lb;
        this.thenStatements = thenStatements;
        this.rb = rb;
        this.elseIfs = elseIfs;
        this.elseToken = elseToken;
        this.elseLB = elseLB;
        this.elseStatements = elseStatements;
        this.elseRB = elseRB;
    }
}