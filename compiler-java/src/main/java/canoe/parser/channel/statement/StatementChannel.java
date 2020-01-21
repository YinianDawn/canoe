package canoe.parser.channel.statement;

import canoe.ast.expression.Expression;
import canoe.ast.expression.ExpressionID;
import canoe.ast.expression.ExpressionOpRight;
import canoe.ast.merge.MergeAssign;
import canoe.ast.merge.MergeOperatorRight;
import canoe.ast.statement.Statement;
import canoe.ast.statement.StatementAssign;
import canoe.ast.statement.StatementEmpty;
import canoe.ast.statement.StatementExpression;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.TokenStream;
import canoe.parser.channel.Channel;
import canoe.parser.channel.expression.ExpressionChannel;

import static canoe.lexer.KindSet.*;

/**
 * @author dawn
 */
public class StatementChannel extends Channel {

    public StatementChannel(Channel channel, Kind... end) {
        this(channel.getName(), channel.getStream(), end);
    }

    public StatementChannel(String name, TokenStream stream, Kind... end) {
        super(name, stream, end);
        Token next = glance();
        if (ASSIGN_OPERATOR.contains(next.kind)) {
            panic("statement can not start with: " + next, next);
        }
        switch (next.kind) {
            case PACKAGE: panic("package key word must be first word if there package is.", next); break;
            case IMPORT: panic("import key word must after package statement.", next); break;
            case SPACES: case CR:
                panic("statement can not start with: " + next, next);
            default:
        }
        mark();
        while (!full()) { eat(); }
        forget();
    }

    public Statement get() {
        if (!full()) {
            panic("statement is not done.");
        }
        return 0 < channel.size() ? (Statement) channel.getLast() : new StatementEmpty();
    }

    private boolean full() {
        if (1 <= channel.size()) {
            if (1 < channel.size() || !(channel.getLast() instanceof Statement)) { return false; }
        }
        return end(glanceSkipSpace());
    }

    private void eat() {
        Token next = glance();
        if (eatSpaceOrCR(next)) {
            next(); eat(); return;
        }

        if (BINARY_OPERATOR.contains(next.kind)) {
            // 语句解析里面有二元操作符
            recover();
            Expression expression = new ExpressionChannel(this, extend(Kind.CR)).get();
            channel.clear();
            channel.addLast(new StatementExpression(expression));
            removeSpace();
            mark();
            return;
        }
        if (channel.isEmpty() && CONSTANT.contains(next.kind)) {
            Expression expression = new ExpressionChannel(this, extend(Kind.CR)).get();
            channel.addLast(new StatementExpression(expression));
            removeSpaceOrCR();
            return;
        }

        if (ASSIGN_OPERATOR.contains(next.kind) || RIGHT_OPERATOR.contains(next.kind)) {

        } else {
            switch (next.kind) {
                // 直接指明的特殊语句，直接吃
                case MATCH: channel.addLast(new MatchChannel(getName(), getStream()).get()); return;
                case IF: channel.addLast(new IfChannel(getName(), getStream()).get()); return;

                case ID:

                    break;


                default: panic("can not be: " + next, next);
            }
        }
        channel.addLast(next());
        digest();
    }

    private void digest() {

        if (digest1()) { digest(); return; }
        if (digest2()) { digest(); return; }

        String status = status();

        switch (status) {
            case "StatementAssign": break;

            case "ID":
                channel.addLast(new ExpressionID((Token) channel.removeLast()));
                accept(true, false); break;
            case "ExpressionOpRight":
            case "ExpressionID":
                if (end(glanceSkipSpace())) {
                    channel.addLast(new StatementExpression((Expression) channel.removeLast()));
                    break;
                } else {
                    accept(true, false); break;
                }


            case "ExpressionID MergeAssign":
                break;



            default: panic("wrong statement.", current());
        }
    }

    private boolean digest2() {
        if (channel.size() <= 1) { return false; }
        Object o1 = channel.removeLast();
        Object o2 = channel.removeLast();
        String status = getKind(o2) + " " + getKind(o1);
        switch (status) {
            case "ExpressionID MergeAssign":
                removeSpace();
                Expression expression = new ExpressionChannel(this, Kind.CR).get();
                channel.addLast(new StatementAssign((Expression) o2, ((MergeAssign) o1).getToken(), expression));
                return true;
            case "ExpressionID MergeOperatorRight":
                channel.addLast(new ExpressionOpRight((Expression) o2, ((MergeOperatorRight) o1).getToken()));
                return true;
//            case "BIT_NOT ExpressionBool": objects.addLast(new ExpressionLeftOp((Token) o2, (Expression) o1)); return true;
//            case "ExpressionID ADD_ADD":
//            case "ExpressionRightOp ADD_ADD": objects.addLast(new ExpressionRightOp((Expression) o2, (Token) o1)); return true;
//            case "ExpressionID ExpressionRoundBracket": objects.addLast(new ExpressionFunction((Expression) o2, (ExpressionRoundBracket) o1)); return true;
//            case "LR RR":
//                objects.addLast(new ExpressionRoundBracket((Token) o2, new ExpressionEmpty(),(Token) o1)); return true;
//
            default:
        }
        channel.addLast(o2);
        channel.addLast(o1);
        return false;
    }

    private boolean digest1() {
        if (channel.isEmpty()) { return false; }
        Object o1 = channel.removeLast();

        if (o1 instanceof Token) {
            Token token = (Token) o1;
            if (ASSIGN_OPERATOR.contains(token.kind)) {
                channel.addLast(new MergeAssign(token));
                return true;
            }
            if (RIGHT_OPERATOR.contains(token.kind)) {
                channel.addLast(new MergeOperatorRight(token));
                return true;
            }
        }

        String status = getKind(o1);
        switch (status) {
            case "ID":
                channel.addLast(new ExpressionID((Token) o1));
                return true;
            default:
        }
        channel.addLast(o1);
        return false;
    }



}
