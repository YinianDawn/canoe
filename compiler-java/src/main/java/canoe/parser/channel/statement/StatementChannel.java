package canoe.parser.channel.statement;


import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.statement.Statement;
import canoe.parser.syntax.statement.StatementEmpty;

import static canoe.lexer.KindSet.OPERATOR_ASSIGN;

/**
 * @author dawn
 */
public class StatementChannel extends Channel<Statement> {

    private StatementChannel(Channel channel, Kind... end) {
        super(channel, end);
        Token next = glance();
        if (OPERATOR_ASSIGN.contains(next.kind)) {
            panic("statement can not start with: " + next, next);
        }
        switch (next.kind) {
            case SPACES: case CR:
                panic("statement can not start with: " + next, next);
            default:
        }
        if (over(next)) {
            data = new StatementEmpty(current(), glance());
        } else {
            mark();
            init();
            forget();
        }
    }

    @Override
    protected boolean eat(Token next) {
//        switch (next.kind) {
//            case PACKAGE: panic("package key word must be first word if there package is.", next); break;
//            case IMPORT: panic("import key word must after package statement.", next); break;
//            case SPACES: case CR:
//                panic("statement can not start with: " + next, next);
//            default:
//        }
//        if (isChannelEmpty() && CONSTANT.contains(next.kind)) {
//            Expression expression = ExpressionChannel.produce(this, extend(Kind.CR));
//            removeSpaceOrCR();
//            data = new StatementExpression(expression);
//            return false;
//        }
//        if (MIDDLE_OPERATOR.contains(next.kind) && next.not(Kind.COMMA)) {
//            // 语句解析里面有二元操作符
//            recover();
//            Expression expression = ExpressionChannel.produce(this, extend(Kind.CR));
//            removeSpaceOrCR();
//            data = new StatementExpression(expression);
//            mark();
//            return false;
//        }
//
//        if (contains(next, ASSIGN_OPERATOR, RIGHT_OPERATOR)) {
//
//        } else {
//            switch (next.kind) {
//                // 直接能确定语句类型的就不一个一个吃了
//                case MATCH: data = MatchChannel.produce(this, extend(Kind.CR));return false;
//                case IF: data = IfChannel.produce(this, extend(Kind.CR)); return false;
//                case LOOP: data = LoopChannel.produce(this, extend(Kind.CR)); return false;
//                case EACH: data = EachChannel.produce(this, extend(Kind.CR)); return false;
//                case FOR: data = ForChannel.produce(this, extend(Kind.CR)); return false;
//                case GOTO:
//                    Token gotoToken = next();
//                    removeSpace();
//                    if (glance().not(Kind.ID)) {
//                        panic("must be id after goto.", glance());
//                    }
//                    Token id = next();
//                    removeSpaceOrCR();
//                    data = new StatementGoto(gotoToken, id);
//                    return false;
//
//
//                case COLON:
//                    // 有 : 是个表达式
//                    recover();
//                    data = new StatementExpression(ExpressionChannel.produce(this, extend()));
//                    mark();
//                    return false;
//                case LR:
//                    // 有 ( 是个表达式
//                    recover();
//                    data = new StatementExpression(ExpressionChannel.produce(this, extend(Kind.COMMA)));
//                    mark();
//                    return false;
//
//                case COMMA:
//                case RETURN:
//                case BREAK:
//                case CONTINUE:
//                case ID:
//                    break;
//
//                case LB:
//                    // { 开始的是个表达式
//                    if (isChannelEmpty()) {
//                        data = new StatementExpression(ExpressionChannel.produce(this, extend(Kind.COMMA)));
//                        return false;
//                    }
//
//                default: panic("???", next);
//            }
//        }
        return true;
    }

    @Override
    protected void digest() {
//        if (digest1()) { digest(); return; }
//        if (digest2()) { digest(); return; }
//        if (digest3()) { digest(); return; }
//
//        String status = status();
//
//        switch (status) {
//            case "ExpressionID":
//            case "StatementAssign":
//            case "ExpressionOpRight":
//            case "StatementLoopLabel":
//            case "StatementReturn":
//            case "ExpressionOpMiddle":
//                break;
//            case "ExpressionID COMMA":
//                break;
//
//            default: panic("wrong statement.");
//        }
    }

    private boolean digest3() {
//        if (channelSizeLess(3)) { return false; }
//        Object o1 = removeLast();
//        Object o2 = removeLast();
//        Object o3 = removeLast();
//        String status = parseName(o3) + " " + parseName(o2) + " " + parseName(o1);
//
//        Token op;
//        switch (status) {
//            case "ExpressionID COMMA ExpressionID":
//                // 检查运算符优先级
//                op = (Token) o2;
//                if (priority(op, glanceSkipSpace())) {
//                    addLast(new ExpressionOpMiddle((Expression) o3, op, (Expression) o1));
//                    return true;
//                }
//                break;
//
//            default:
//        }
//        addLast(o3);
//        addLast(o2);
//        addLast(o1);
        return false;
    }

    private boolean digest2() {
//        if (channelSizeLess(2)) { return false; }
//        Object o1 = removeLast();
//        Object o2 = removeLast();
//        String status = parseName(o2) + " " + parseName(o1);
//        switch (status) {
//            case "ExpressionOpMiddle MergeAssign":
//            case "ExpressionID MergeAssign":
//                removeSpace();
//                Expression expression = ExpressionChannel.produce(this, extend(Kind.CR));
//                addLast(new StatementAssign((Expression) o2, ((MergeAssign) o1).getToken(), expression));
//                ignoreSpace().refuseCR().over(this::full);
//                return true;
//            case "ExpressionID MergeOperatorRight":
//                addLast(new ExpressionOpRight((Expression) o2, ((MergeOperatorRight) o1).getToken()));
//                ignoreSpace().refuseCR().over(this::full);
//                return true;
//            default:
//        }
//        addLast(o2);
//        addLast(o1);
        return false;
    }

    private boolean digest1() {
//        if (isChannelEmpty()) { return false; }
//        Object o1 = removeLast();
//
//        if (o1 instanceof Token) {
//            Token token = (Token) o1;
//            if (ASSIGN_OPERATOR.contains(token.kind)) {
//                addLast(new MergeAssign(token));
//                ignoreSpace();
//                return true;
//            }
//            if (RIGHT_OPERATOR.contains(token.kind)) {
//                addLast(new MergeOperatorRight(token));
//                ignoreSpace();
//                return true;
//            }
//            if (token.is(Kind.ID)) {
//                addLast(new ExpressionID((Token) o1));
//                ignoreSpace();
//                if (channelSize(1)) { over(this::full); }
//                return true;
//            }
//            if (token.is(Kind.RETURN)) {
//                removeSpace();
//                Expression expression;
//                if (glance().isCR()) {
//                    expression = new ExpressionEmpty(current(), glance());
//                } else {
//                    expression = ExpressionChannel.produce(this, extend(Kind.CR));
//                }
//                removeSpace();
//                addLast(new StatementReturn(token, expression));
//                over(this::full);
//                return true;
//            }
//            if (token.is(Kind.BREAK) || token.is(Kind.CONTINUE)) {
//                removeSpace();
//                Token id = null;
//                Token next = glance();
//                if (next.is(Kind.ID)) {
//                    id = next();
//                    removeSpace();
//                    next = glance();
//                }
//                if (end(next)) {
//                    addLast(new StatementLoopLabel(token, id));
//                    over(this::full);
//                } else {
//                    panic("can not be here.", next);
//                }
//                return true;
//            }
//            if (token.is(Kind.COMMA)) {
//                removeSpace();
//            }
//        }
//
////        String status = parseName(o1);
////        switch (status) {
////
////            default:
////        }
//        addLast(o1);
        return false;
    }

    private void full() {
//        if (channelSize(1) ) {
//            Object o = getLast();
//            if (o instanceof Statement) {
//                data = (Statement) removeLast();
//            } else if (o instanceof Expression) {
//                data = new StatementExpression((Expression) o);
//            }
//            if (null != data) {
//                return;
//            }
//        }
        panic("should not end.");
    }

    public static Statement make(Channel channel, Kind... end) {
        return new StatementChannel(channel, end).make();
    }

}
