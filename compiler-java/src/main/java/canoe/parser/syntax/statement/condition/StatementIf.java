package canoe.ast.statement.condition;


import canoe.ast.expression.Expression;
import canoe.ast.statement.Statement;
import canoe.ast.statement.Statements;
import canoe.ast.statement.condition.elseif.ElseIf;
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

    @Override
    public Token first() {
        return ifToken;
    }

    @Override
    public Token last() {
        return null != elseToken ? elseRB : ( elseIfs.isEmpty() ? rb : elseIfs.get(elseIfs.size() - 1).last());
    }
}
