package canoe.parser.channel.statement;

import canoe.ast.expression.Expression;
import canoe.ast.statement.*;
import canoe.ast.statement.match.MatchClause;
import canoe.ast.statement.match.MatchElseClause;
import canoe.ast.statement.match.MatchOpExpression;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.TokenStream;
import canoe.parser.channel.Channel;
import canoe.parser.channel.expression.ExpressionChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static canoe.lexer.KindSet.CONSTANT;
import static canoe.lexer.KindSet.RELATIONAL_OPERATOR;

/**
 * @author dawn
 */
public class MatchChannel extends Channel {

    private Token matchToken;
    private Token colonToken;
    private Token withToken;
    private Expression expression;
    private Token lb;
    private List<MatchClause> clauses = new ArrayList<>();
    private MatchElseClause elseClause;
    private Token rb;

    private StatementMatch statementMatch;

    public MatchChannel(String name, TokenStream stream) {
        super(name, stream);
        if (stream.glance().not(Kind.MATCH)) {
            panic("must be match.", stream.glance());
        }
        parse();
    }

    public StatementMatch get() {
        return statementMatch;
    }

    private void parse() {
        matchToken = next();
        removeSpace();
        colonToken = glance();
        if (colonToken.isColon()) {
            colonToken = next();
            withToken = next();
            if (withToken.not(Kind.WITH) && withToken.not(Kind.WITHOUT)) {
                panic("match desc must be with or without", withToken);
            }
            if (!colonToken.next(withToken)) {
                panic(withToken.kind.getSign() + " must follow : , no space", withToken);
            }
        } else {
            colonToken = null;
        }
        removeSpace();
        ExpressionChannel channel = new ExpressionChannel(this, Kind.LB);
        expression = channel.get();
        if (null == expression) {
            panic("expression can not be null");
        }
        removeSpaceOrCR();
        lb = next();
        if (lb.not(Kind.LB)) {
            panic("must be { .", lb);
        }
        removeSpaceOrCR();
        parseClauses();
        removeSpaceOrCR();
        rb = next();
        if (rb.not(Kind.RB)) {
            panic("must be } .", rb);
        }
        statementMatch = new StatementMatch(matchToken, colonToken, withToken,
                expression, lb, clauses, elseClause, rb);
    }

    private void parseClauses() {
        Token next = glance();
        while (null != next) {
            if (next.is(Kind.ELSE)) { parseElseClause(); return; }
            if (CONSTANT.contains(next.kind)
                    || RELATIONAL_OPERATOR.contains(next.kind)) {
                parseClause();
            } else {
                switch (next.kind) {
                    case ID:
                        parseClause(); break;
                    default: panic("wrong token.", next);
                }
            }
            removeSpaceOrCR();
            next = glance();
        }
    }

    private void parseClause() {
        Token op = parseMatchOp();
        Expression expression = new ExpressionChannel(this, Kind.COMMA, Kind.COLON).get();

        List<MatchOpExpression> others = new ArrayList<>();
        removeSpace();
        Token token = glance();
        while (token.is(Kind.COMMA)) {
            Token comma = next();
            removeSpaceOrCR();
            Token otherOp = parseMatchOp();
            Expression e = new ExpressionChannel(this, Kind.COMMA, Kind.COLON).get();
            others.add(new MatchOpExpression(comma, otherOp, e));
            removeSpace();
            token = glance();
        }
        removeSpace();
        Token colonToken = next();
        if (colonToken.not(Kind.COLON)) {
            panic("must be : ", colonToken);
        }
        parseStatements();
        clauses.add(new MatchClause(op, expression, others, colonToken, clauseLB, clauseStatements, clauseRB));
    }

    private Token clauseLB;
    private Statements clauseStatements;
    private Token clauseRB;
    private void parseStatements() {
        clauseLB = null;
        clauseStatements = null;
        clauseRB = null;
        removeSpace();
        Token next = glance();
        switch (next.kind) {
            case CR:
                clauseStatements = new Statements(Collections.emptyList());
                removeSpaceOrCR();
                return;
            case WITH:
            case WITHOUT:
                Token withToken = next();
                next = glanceSkipSpace();
                if (!next.isCR()) {
                    panic("must be CR(\\ n)", next);
                }
                clauseStatements = new Statements(Collections.singletonList(new StatementWith(withToken)));
                removeSpaceOrCR();
                return;
            case LB:
                // 有花括号的语句
                clauseLB = next();
                List<Statement> statements = new ArrayList<>();
                // 解析多个语句
                removeSpaceOrCR();
                Statement statement = new StatementChannel(this, Kind.RB).get();
                while (!(statement instanceof StatementEmpty)) {
                    statements.add(statement);
                    removeSpaceOrCR();
                    statement = new StatementChannel(this, Kind.RB).get();
                }
                removeSpaceOrCR();
                clauseRB = next();
                if (clauseRB.not(Kind.RB)) {
                    panic("must be } ", clauseRB);
                }
                removeSpaceOrCR();
                next = glance();
                switch (next.kind){
                    case WITH:
                    case WITHOUT:
                        statements.add(new StatementWith(next()));
                        break;
                    default:
                }
                clauseStatements = new Statements(statements);
                removeSpaceOrCR();
                return;
            case ID:
                Expression expression = new ExpressionChannel(this, Kind.CR).get();
                next = glanceSkipSpace();
                if (!next.isCR() && next.not(Kind.RB)) {
                    panic("expression clause must be end by CR(\\ n) or }.", next);
                }
                clauseStatements = new Statements(Collections.singletonList(new StatementExpression(expression)));
                removeSpaceOrCR();
                return;
            default: panic("can not be this token.", next);
        }
    }

    private Token parseMatchOp() {
        Token op = glance();
        if (RELATIONAL_OPERATOR.contains(op.kind)) {
            op = next();
            removeSpace();
        } else { op = null; }
        return op;
    }

    private void parseElseClause() {
        Token elseToken = next();
        removeSpace();
        Token colonToken = next();
        if (colonToken.not(Kind.COLON)) {
            panic("must be : ", colonToken);
        }
        parseStatements();
        elseClause = new MatchElseClause(elseToken, colonToken, clauseLB, clauseStatements, clauseRB);
    }


}
