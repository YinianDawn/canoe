//package canoe.parser.channel.statement.condition;
//
//import canoe.ast.expression.Expression;
//import canoe.ast.statement.*;
//import canoe.ast.statement.condition.StatementMatch;
//import canoe.ast.statement.condition.StatementWith;
//import canoe.ast.statement.condition.match.MatchClause;
//import canoe.ast.statement.condition.match.MatchElseClause;
//import canoe.ast.statement.condition.match.MatchOpExpression;
//import canoe.lexer.Kind;
//import canoe.lexer.Token;
//import canoe.parser.channel.Channel;
//import canoe.parser.channel.expression.ExpressionChannel;
//import canoe.parser.channel.statement.StatementChannel;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static canoe.lexer.KindSet.*;
//
///**
// * @author dawn
// */
//public class MatchChannel extends Channel<StatementMatch> {
//
//    private Token matchToken;
//    private Token colonToken;
//    private Token withToken;
//    private Expression expression;
//    private Token lb;
//    private List<MatchClause> clauses = new ArrayList<>();
//    private MatchElseClause elseClause;
//    private Token rb;
//
//
//    private MatchChannel(Channel channel, Kind... end) {
//        super(channel, end);
//        if (glance().not(Kind.MATCH)) {
//            panic("must be match.", glance());
//        }
//        init();
//    }
//
//    @Override
//    protected void init() {
//        matchToken = next();
//        removeSpace();
//        colonToken = glance();
//        if (colonToken.isColon()) {
//            colonToken = next();
//            withToken = next();
//            if (withToken.not(Kind.WITH) && withToken.not(Kind.WITHOUT)) {
//                panic("must be with or without", withToken);
//            }
//            if (!colonToken.next(withToken)) {
//                panic(withToken.kind.sign + " must follow sign : , no space", withToken);
//            }
//        } else {
//            colonToken = null;
//        }
//        removeSpace();
//        expression = ExpressionChannel.produce(this, Kind.LB);
//        if (null == expression) {
//            panic("expression can not be null");
//        }
//        removeSpace();
//        lb = next();
//        if (lb.not(Kind.LB)) {
//            panic("must be { .", lb);
//        }
//        removeSpaceOrCR();
//        parseClauses();
//        removeSpaceOrCR();
//        rb = next();
//        if (rb.not(Kind.RB)) {
//            panic("must be } .", rb);
//        }
//        data = new StatementMatch(matchToken, colonToken, withToken,
//                expression, lb, clauses, elseClause, rb);
//        removeEnd();
//    }
//
//    private void parseClauses() {
//        Token next = glance();
//        while (null != next) {
//            if (next.is(Kind.ELSE)) { parseElseClause(); return; }
//            if (contains(next, CONSTANT, RELATION_OPERATOR)) {
//                parseClause();
//            } else {
//                switch (next.kind) {
//                    case IN:
//                    case ID:
//                        parseClause(); break;
//                    default: panic("wrong token.", next);
//                }
//            }
//            removeSpaceOrCR();
//            next = glance();
//        }
//    }
//
//    private void parseClause() {
//        Token op = parseMatchOp();
//        Expression expression = ExpressionChannel.produce(this, Kind.COMMA, Kind.COLON_BLANK);
//
//        List<MatchOpExpression> others = new ArrayList<>();
//        removeSpace();
//        Token token = glance();
//        while (token.is(Kind.COMMA)) {
//            Token comma = next();
//            removeSpaceOrCR();
//            Token otherOp = parseMatchOp();
//            Expression e = ExpressionChannel.produce(this, Kind.COMMA, Kind.COLON_BLANK);
//            others.add(new MatchOpExpression(comma, otherOp, e));
//            removeSpace();
//            token = glance();
//        }
//        removeSpace();
//        Token colonToken = next();
//        if (colonToken.not(Kind.COLON_BLANK)) {
//            panic("must be :< >", colonToken);
//        }
//        ClauseStatements cs = parseClauseStatements();
//
//
//
//
//        if (!end(Kind.SPACES)) { removeSpace(); }
//        if (!end(Kind.CR)) { removeSpaceOrCR(); }
//        clauses.add(new MatchClause(op, expression, others, colonToken,
//                cs.clauseLB, cs.clauseStatements, cs.clauseRB));
//    }
//
//    private Token parseMatchOp() {
//        Token op = glance();
//        if (contains(op, RELATION_OPERATOR) || op.is(Kind.IN)) {
//            op = next();
//            removeSpace();
//        } else { op = null; }
//        return op;
//    }
//
//    private void parseElseClause() {
//        Token elseToken = next();
//        removeSpace();
//        Token colonToken = next();
//        if (colonToken.not(Kind.COLON_BLANK)) {
//            panic("must be :< >", colonToken);
//        }
//        ClauseStatements cs = parseClauseStatements();
//        elseClause = new MatchElseClause(elseToken, colonToken,
//                cs.clauseLB, cs.clauseStatements, cs.clauseRB);
//    }
//
//    private ClauseStatements parseClauseStatements() {
//        Token clauseLB = null;
//        Statements clauseStatements = null;
//        Token clauseRB = null;
//        removeSpace();
//        Token next = glance();
//        switch (next.kind) {
//            case CR:
//                clauseStatements = new Statements(Collections.emptyList());
//                removeSpaceOrCR(); break;
//            case WITH: case WITHOUT:
//                Token withToken = next();
//                next = glanceSkipSpace();
//                if (!next.isCR()) {
//                    panic("must be CR(\\ n)", next);
//                }
//                clauseStatements = new Statements(Collections.singletonList(new StatementWith(withToken)));
//                removeSpaceOrCR();
//                break;
//            case LB:
//                // 有花括号的语句
//                clauseLB = next();
//                List<Statement> statements = new ArrayList<>();
//                // 解析多个语句
//                removeSpaceOrCR();
//                Statement statement = StatementChannel.produce(this, Kind.RB);
//                while (!(statement instanceof StatementEmpty)) {
//                    statements.add(statement);
//                    removeSpaceOrCR();
//                    statement = StatementChannel.produce(this, Kind.RB);
//                }
//                removeSpaceOrCR();
//                clauseRB = next();
//                if (clauseRB.not(Kind.RB)) {
//                    panic("must be } ", clauseRB);
//                }
//                removeSpaceOrCR();
//                switch (glance().kind){
//                    case WITH: case WITHOUT:
//                        if (!glanceSkipSpace().isCR()) {
//                            panic("must be CR(\\ n) ", glanceSkipSpace());
//                        }
//                        statements.add(new StatementWith(next()));
//                        break;
//                    default:
//                }
//                clauseStatements = new Statements(statements);
//                removeSpaceOrCR();
//                break;
//            case ID:
//                Expression expression = ExpressionChannel.produce(this, Kind.CR, Kind.RB, Kind.WITH, Kind.WITHOUT);
//                next = glanceSkipSpace();
//                withToken = null;
//                if (next.is(Kind.WITH, Kind.WITHOUT)) {
//                    withToken = next();
//                    next = glanceSkipSpace();
//                }
//                if (!next.isCR() && next.not(Kind.RB)) {
//                    panic("expression clause must be end by CR(\\ n) or }.", next);
//                }
//                if (null == withToken) {
//                    clauseStatements = new Statements(Collections.singletonList(new StatementExpression(expression)));
//                } else {
//                    clauseStatements = new Statements(Arrays.asList(new StatementExpression(expression), new StatementWith(withToken)));
//                }
//                removeSpaceOrCR();
//                break;
//            default: panic("can not be this token.", next);
//        }
//        return new ClauseStatements(clauseLB, clauseStatements, clauseRB);
//    }
//
//    private static class ClauseStatements {
//        private Token clauseLB;
//        private Statements clauseStatements;
//        private Token clauseRB;
//        ClauseStatements(Token clauseLB, Statements clauseStatements, Token clauseRB) {
//            this.clauseLB = clauseLB;
//            this.clauseStatements = clauseStatements;
//            this.clauseRB = clauseRB;
//        }
//    }
//
//    public static StatementMatch produce(Channel channel, Kind... end) {
//        return new MatchChannel(channel, end).produce();
//    }
//
//}
