package canoe.parser.syntax.statement.condition;

import canoe.lexer.Token;
import canoe.parser.syntax.Statements;
import canoe.parser.syntax.expression.Expression;
import canoe.parser.syntax.statement.Statement;
import canoe.parser.syntax.statement.condition.elseif.ElseIf;

import java.util.List;

/**
 * @author dawn
 */
public class StatementIf implements Statement {

    private Token IF;
    private Expression condition;
    private Token LB;
    private Statements thenStatements;
    private Token RB;
    private List<ElseIf> elseIfs;
    private Token ELSE;
    private Token ELSE_LB;
    private Statements elseStatements;
    private Token ELSE_RB;

    public StatementIf(Token symbol, Expression condition,
                       Token lb, Statements thenStatements, Token rb,
                       List<ElseIf> elseIfs,
                       Token elseToken, Token elseLb, Statements elseStatements, Token elseRb) {
        this.IF = symbol;
        this.condition = condition;
        this.LB = lb;
        this.thenStatements = thenStatements;
        this.RB = rb;
        this.elseIfs = elseIfs;
        this.ELSE = elseToken;
        this.ELSE_LB = elseLb;
        this.elseStatements = elseStatements;
        this.ELSE_RB = elseRb;
    }

    @Override
    public Token first() {
        return IF;
    }

    @Override
    public Token last() {
        return null != ELSE ? ELSE_RB : (elseIfs.isEmpty() ? RB : elseIfs.get(elseIfs.size() - 1).last());
    }
}
