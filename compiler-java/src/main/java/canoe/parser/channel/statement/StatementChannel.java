package canoe.parser.channel.statement;

import canoe.ast.expression.Expression;
import canoe.ast.expression.ExpressionID;
import canoe.ast.expression.ExpressionOpRight;
import canoe.ast.merge.MergeAssign;
import canoe.ast.merge.MergeOperatorRight;
import canoe.ast.statement.*;
import canoe.ast.statement.loop.StatementLoopLabel;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.channel.expression.ExpressionChannel;
import canoe.parser.channel.statement.condition.IfChannel;
import canoe.parser.channel.statement.condition.MatchChannel;
import canoe.parser.channel.statement.loop.EachChannel;
import canoe.parser.channel.statement.loop.ForChannel;
import canoe.parser.channel.statement.loop.LoopChannel;

import static canoe.lexer.KindSet.*;

/**
 * @author dawn
 */
public class StatementChannel extends Channel<Statement> {

    private StatementChannel(Channel channel, Kind... end) {
        super(channel, end);
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
        if (end(next)) {
            data = new StatementEmpty(current(), glance());
        } else {
            mark();
            init();
            forget();
        }
    }

    @Override
    protected boolean eat(Token next) {
        if (isChannelEmpty() && CONSTANT.contains(next.kind)) {
            Expression expression = ExpressionChannel.produce(this, extend(Kind.CR));
            removeSpaceOrCR();
            data = new StatementExpression(expression);
            return false;
        }
        if (MIDDLE_OPERATOR.contains(next.kind)) {
            // 语句解析里面有二元操作符
            recover();
            Expression expression = ExpressionChannel.produce(this, extend(Kind.CR));
            removeSpaceOrCR();
            data = new StatementExpression(expression);
            mark();
            return false;
        }

        if (contains(next, ASSIGN_OPERATOR, RIGHT_OPERATOR)) {

        } else {
            switch (next.kind) {
                // 直接能确定语句类型的就不一个一个吃了
                case MATCH: data = MatchChannel.produce(this, extend(Kind.CR));return false;
                case IF: data = IfChannel.produce(this, extend(Kind.CR)); return false;
                case LOOP: data = LoopChannel.produce(this, extend(Kind.CR)); return false;
                case EACH: data = EachChannel.produce(this, extend(Kind.CR)); return false;
                case FOR: data = ForChannel.produce(this, extend(Kind.CR)); return false;

                case LB:
                    // { 开始的是个表达式
                    data = new StatementExpression(ExpressionChannel.produce(this, extend(Kind.COMMA)));
                    return false;

                case RETURN:
                case BREAK:
                case CONTINUE:
                case ID:
                    break;
                default: panic("???", next);
            }
        }
        return true;
    }

    @Override
    protected void digest() {
        if (digest1()) { digest(); return; }
        if (digest2()) { digest(); return; }

        String status = status();

        switch (status) {
            case "ExpressionID":
            case "StatementAssign":
            case "ExpressionOpRight":
            case "StatementLoopLabel":
            case "StatementReturn":
                break;

            default: panic("wrong statement.");
        }
    }

    private boolean digest2() {
        if (channelSizeLess(2)) { return false; }
        Object o1 = removeLast();
        Object o2 = removeLast();
        String status = parseName(o2) + " " + parseName(o1);
        switch (status) {
            case "ExpressionID MergeAssign":
                removeSpace();
                Expression expression = ExpressionChannel.produce(this, extend(Kind.CR));
                addLast(new StatementAssign((Expression) o2, ((MergeAssign) o1).getToken(), expression));
                ignoreSpace().refuseCR().over(this::full);
                return true;
            case "ExpressionID MergeOperatorRight":
                addLast(new ExpressionOpRight((Expression) o2, ((MergeOperatorRight) o1).getToken()));
                ignoreSpace().refuseCR().over(this::full);
                return true;
////            case "BIT_NOT ExpressionBool": objects.addLast(new ExpressionLeftOp((Token) o2, (Expression) o1)); return true;
////            case "ExpressionID ADD_ADD":
////            case "ExpressionRightOp ADD_ADD": objects.addLast(new ExpressionRightOp((Expression) o2, (Token) o1)); return true;
////            case "ExpressionID ExpressionRoundBracket": objects.addLast(new ExpressionFunction((Expression) o2, (ExpressionRoundBracket) o1)); return true;
////            case "LR RR":
////                objects.addLast(new ExpressionRoundBracket((Token) o2, new ExpressionEmpty(),(Token) o1)); return true;
////
            default:
        }
        addLast(o2);
        addLast(o1);
        return false;
    }

    private boolean digest1() {
        if (isChannelEmpty()) { return false; }
        Object o1 = removeLast();

        if (o1 instanceof Token) {
            Token token = (Token) o1;
            if (ASSIGN_OPERATOR.contains(token.kind)) {
                addLast(new MergeAssign(token));
                ignoreSpace();
                return true;
            }
            if (RIGHT_OPERATOR.contains(token.kind)) {
                addLast(new MergeOperatorRight(token));
                ignoreSpace();
                return true;
            }
            if (token.is(Kind.ID)) {
                addLast(new ExpressionID((Token) o1));
                ignoreSpace();
                if (channelSize(1)) { over(this::full); }
                return true;
            }
            if (token.is(Kind.RETURN)) {
                removeSpace();
                Expression expression = ExpressionChannel.produce(this, extend(Kind.CR));
                removeSpace();
                addLast(new StatementReturn(token, expression));
                over(this::full);
                return true;
            }
            if (token.is(Kind.BREAK) || token.is(Kind.CONTINUE)) {
                removeSpace();
                Token id = null;
                Token next = glance();
                if (next.is(Kind.ID)) {
                    id = next();
                    removeSpace();
                    next = glance();
                }
                if (end(next)) {
                    addLast(new StatementLoopLabel(token, id));
                    over(this::full);
                } else {
                    panic("can not be here.", next);
                }
                return true;
            }
        }

//        String status = parseName(o1);
//        switch (status) {
//
//            default:
//        }
        addLast(o1);
        return false;
    }

    private void full() {
        if (channelSize(1) ) {
            Object o = getLast();
            if (o instanceof Statement) {
                data = (Statement) removeLast();
            } else if (o instanceof Expression) {
                data = new StatementExpression((Expression) o);
            }
            if (null != data) {
                return;
            }
        }
        panic("should not end.");
    }

    public static Statement produce(Channel channel, Kind... end) {
        return new StatementChannel(channel, end).produce();
    }

}
