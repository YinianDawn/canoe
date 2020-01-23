package canoe.parser.channel.expression;

import canoe.ast.expression.*;
import canoe.ast.merge.*;
import canoe.ast.statement.Statements;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.channel.statement.condition.IfChannel;
import canoe.parser.channel.statement.condition.MatchChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static canoe.lexer.KindSet.*;

/**
 * @author dawn
 */
public class ExpressionChannel extends Channel<Expression> {

    private ExpressionChannel(Channel channel, Kind... end) {
        super(channel, end);
        Token next = glance();
        if (!SINGLE_KEY_WORDS.contains(next.kind)
                && !LEFT_OPERATOR.contains(next.kind)
                && !CONSTANT.contains(next.kind)) {
//            if (end(next)) { panic("can not start with " + next.kind.sign, next); }
            switch (next.kind) {
                case LB: case RB:
                case LR: case RR:
                case LS: case RS:
                case ID: case COLON: case DOT_DOT_DOT:
                    break;
                default: panic("can not be this kind of token.", next);
            }
        }
        init();
    }

    @Override
    protected boolean eat(Token next) {
        switch (next.kind) {
            // 直接能确定语句类型的就不一个一个吃了
            case MATCH: data = new ExpressionMatch(MatchChannel.produce(this, extend())); return false;
            case IF: data = new ExpressionIf(IfChannel.produce(this, extend())); return false;
            case RR: case RB: case RS:
                data = new ExpressionEmpty(current(), next); return false;


            default:
        }
        // 检查关键词
        if (SINGLE_KEY_WORDS.contains(next.kind) || next.kind == Kind.ELSE_IF) {
            boolean pass = false;
            if (CONSTANT.contains(next.kind)) { pass = true; }
            if (isChannelFull()) {
                Object o = getLast();
                if (o instanceof Token) {
                    switch (((Token) o).kind) {
                        case DOT:  pass = true; break;
                        case COLON:
                            switch (next.kind) {
                                case NATIVE:
                                case GOTO:
                                    pass = true;
                                    break;
                                default:
                            }
                            break;
                        default:
                    }
                } else if (o instanceof MergeOperatorBoth && ((MergeOperatorBoth) o).getToken().is(Kind.DOT)) {
                    pass = true;
                }
            }
            if (isChannelEmpty()) {
                mark();
                next();
                if (glance().is(Kind.DOT)) { pass = true; }
                recover();
            }
            if (!pass) {
                panic("should be here.", next);
            }
        }
        if (end(next) && isChannelFull()) {
            panic("should be here.", next);
        }
        return true;
    }

    @Override
    protected void digest() {
        if (digest1()) { digest(); return; }
        if (digest2()) { digest(); return; }
        if (digest3()) { digest(); return; }

        String status = status();
        switch (status) {
            // 单个表达式
            case "ExpressionConstant":
            case "ExpressionID":
            case "ExpressionOpMiddle":
            case "ExpressionOpRight":
            case "ExpressionOpLeft":
            case "ExpressionFunctionCall":
            case "ExpressionFunction":
            case "ExpressionStruct":
            case "ExpressionSquareBracket":
            case "ExpressionRoundBracket":
            case "ExpressionArray":
            case "ExpressionLambda":
            case "ExpressionCast":
            case "ExpressionKeyWord":
            case "ExpressionWithTraits":
            case "ExpressionCommaTraits":
            case "ExpressionChangeID":
            case "ExpressionOperatorOverload":
                break;
            // 单个运算符
            case "MergeOperatorLeft":
            case "MergeOperatorBoth":
            case "MergeOperatorOverload":
                break;
            // 单个约束
            case "ExpressionTraits":
                break;

            // 表达式 运算符
            case "ExpressionID MergeOperatorBoth":
            case "ExpressionOpMiddle MergeOperatorBoth":
            case "ExpressionConstant MergeOperatorBoth":
            case "ExpressionFunctionCall MergeOperatorBoth":
            case "ExpressionKeyWord MergeOperatorBoth":
            case "ExpressionOpMiddle LR":
            case "ExpressionID LR":
            case "ExpressionOpMiddle ExpressionRoundBracket":
            case "ExpressionSquareBracket LB":
            case "ExpressionWithTraits MergeOperatorBoth":
            case "ExpressionTraits MergeOperatorBoth":
                break;

            // 表达式 约束
            case "ExpressionStruct ExpressionTraits":
            case "ExpressionArray ExpressionTraits":
            case "ExpressionID ExpressionTraits":
                break;

            // 运算符重载 约束
            case "MergeOperatorOverload ExpressionTraits":
                break;

            // 运算符 表达式
            case "LB Statements":
            case "LS ExpressionConstant":
            case "LR ExpressionConstant":
                break;

            // 表达式 运算符 表达式
            case "ExpressionOpMiddle LR ExpressionConstant":
            case "ExpressionOpMiddle LR ExpressionOpMiddle":
            case "ExpressionID LR ExpressionConstant":
            case "ExpressionID MergeOperatorBoth ExpressionID":
            case "ExpressionFunctionCall MergeOperatorBoth ExpressionID":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID":
            case "ExpressionID LR ExpressionID":
            case "ExpressionID LR ExpressionOpMiddle":
            case "ExpressionID MergeOperatorBoth ExpressionChangeID":
            case "ExpressionWithTraits MergeOperatorBoth ExpressionID":
            case "ExpressionWithTraits MergeOperatorBoth ExpressionChangeID":
            case "ExpressionWithTraits MergeAssign ExpressionFunction":
            case "ExpressionWithTraits MergeAssign ExpressionLambda":
            case "ExpressionTraits MergeOperatorBoth ExpressionChangeID":
            case "ExpressionWithTraits MergeAssign ExpressionID":
            case "ExpressionOpMiddle MergeAssign ExpressionFunctionCall":
                break;

            // 表达式 运算符 运算符
            case "ExpressionID MergeOperatorBoth LR":
                break;

            // 表达式 多约束 运算符
            case "ExpressionID ExpressionTraits MergeOperatorBoth":
                break;
            // 多约束 表达式 多约束
            case "ExpressionTraits MergeOperatorBoth ExpressionTraits":
                break;

            // 表达式 运算符 运算符 表达式
            case "ExpressionID MergeOperatorBoth LR ExpressionID":
                break;

            // 表达式 运算符 表达式 运算符
            case "ExpressionID MergeOperatorBoth ExpressionID LR":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID LR":
            case "ExpressionFunctionCall MergeOperatorBoth ExpressionID LR":
            case "ExpressionID MergeOperatorBoth ExpressionID MergeOperatorBoth":
            case "ExpressionID LR ExpressionID MergeOperatorBoth":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID MergeOperatorBoth":
                break;

            // 运算符重载 多约束 赋值 对象
            case "MergeOperatorOverload ExpressionTraits MergeAssign ExpressionFunction":
                break;

            // 表达式 运算符 表达式 运算符 表达式
            case "ExpressionID MergeOperatorBoth ExpressionID LR ExpressionConstant":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID LR ExpressionConstant":
            case "ExpressionID MergeOperatorBoth ExpressionID LR ExpressionOpMiddle":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID LR ExpressionOpMiddle":
            case "ExpressionID LR ExpressionID MergeOperatorBoth ExpressionID":
                break;

//                break;

            // 表达式 运算符 表达式 运算符 表达式 运算符
            case "ExpressionID MergeOperatorBoth ExpressionID LR ExpressionConstant MergeOperatorBoth":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID LR ExpressionConstant MergeOperatorBoth":
            case "ExpressionID LR ExpressionID MergeOperatorBoth ExpressionID LR":
                break;

            default: panic("wrong expression.");
        }
    }


    private boolean digest3() {
        if (channelSizeLess(3)) { return false; }
        Object o1 = removeLast();
        Object o2 = removeLast();
        Object o3 = removeLast();
        String status = parseName(o3) + " " + parseName(o2) + " " + parseName(o1);

        Token op;
        switch (status) {
            case "ExpressionID MergeOperatorBoth ExpressionID":
            case "ExpressionKeyWord MergeOperatorBoth ExpressionID":
            case "ExpressionID MergeOperatorBoth ExpressionConstant":
            case "ExpressionConstant MergeOperatorBoth ExpressionConstant":
            case "ExpressionID MergeOperatorBoth ExpressionFunctionCall":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionFunctionCall":
            case "ExpressionFunctionCall MergeOperatorBoth ExpressionFunctionCall":
            case "ExpressionID MergeOperatorBoth ExpressionOpMiddle":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionStruct":
            case "ExpressionID MergeOperatorBoth ExpressionRoundBracket":
            case "ExpressionWithTraits MergeOperatorBoth ExpressionWithTraits":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionConstant":
            case "ExpressionConstant MergeOperatorBoth ExpressionID":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionOpMiddle":
                // 检查运算符优先级
                op = ((MergeOperatorBoth) o2).getToken();
                if (priority(op, glanceSkipSpace())) {
                    if (op.is(Kind.LAMBDA)) {
                        // lambda 要特别对待
                        addLast(new ExpressionLambda((Expression) o3, op, (Expression) o1));
                    } else if (op.is(Kind.DOT) && o1 instanceof ExpressionRoundBracket) {
                        addLast(new ExpressionCast((Expression) o3, op, (ExpressionRoundBracket) o1));
                    } else {
                        addLast(new ExpressionOpMiddle((Expression) o3, op, (Expression) o1));
                    }
                    return true;
                }
                break;
            case "ExpressionTraits MergeOperatorBoth ExpressionTraits":
            case "ExpressionTraits MergeOperatorBoth ExpressionWithTraits":
                // 检查运算符优先级
                op = ((MergeOperatorBoth) o2).getToken();
                if (op.is(Kind.COMMA) && priority(op, glanceSkipSpace())) {
                    addLast(new ExpressionCommaTraits((ExpressionTraits) o3, op, (Expression) o1));
                    return true;
                }
                break;
            case "LR ExpressionID RR":
            case "LR ExpressionOpMiddle RR":
            case "LR ExpressionConstant RR":
                addLast(new ExpressionRoundBracket((Token) o3, (Expression) o2, (Token) o1));
                return true;
            case "LS ExpressionConstant RS":
                addLast(new ExpressionSquareBracket((Token) o3, (Expression) o2, (Token) o1));
                return true;

            default:
        }
        addLast(o3);
        addLast(o2);
        addLast(o1);
        return false;
    }

    private boolean digest2() {
        if (channelSizeLess(2)) { return false; }
        Object o1 = removeLast();
        Object o2 = removeLast();
        String status = parseName(o2) + " " + parseName(o1);
        switch (status) {
            case "ExpressionOpMiddle MergeOperatorRight":
                addLast(new ExpressionOpRight((Expression) o2, ((MergeOperatorRight) o1).getToken()));
                tryRemoveSpace();
                return true;
            case "ExpressionID ExpressionRoundBracket":
                addLast(new ExpressionFunctionCall((ExpressionID) o2, (ExpressionRoundBracket) o1));
                tryRemoveSpace();
                return true;
            case "ExpressionRoundBracket ExpressionStruct":
                addLast(new ExpressionFunction((ExpressionRoundBracket) o2, (ExpressionStruct) o1));
                tryRemoveSpace();
                return true;
            case "ExpressionSquareBracket ExpressionStruct":
                ExpressionStruct struct = (ExpressionStruct) o1;
                ExpressionSquareBracket squareBracket = (ExpressionSquareBracket) o2;
                if (!squareBracket.last().next(struct.first())) {
                    panic("should be connected. " + squareBracket.last() + " " + struct.first());
                }
                addLast(new ExpressionArray(squareBracket, struct));
                tryRemoveSpace().acceptColon().refuseAll();
                return true;
            case "MergeOperatorLeft ExpressionConstant":
            case "MergeOperatorLeft ExpressionRoundBracket":
            case "MergeOperatorLeft ExpressionID":
                MergeOperatorLeft left = (MergeOperatorLeft) o2;
                String leftOtherSign = left.getToken().is(Kind.ADD, Kind.SUB) ? "l" : null;
                if (priority(left.getToken(), glanceSkipSpace(), leftOtherSign)) {
                    addLast(new ExpressionOpLeft(left.getToken(), (Expression) o1));
                    tryRemoveSpace().acceptColon();
                    return true;
                }
            case "ExpressionOpRight MergeOperatorRight":
            case "ExpressionID MergeOperatorRight":
                MergeOperatorRight right = (MergeOperatorRight) o1;
                if (priority(right.getToken(), glanceSkipSpace())) {
                    addLast(new ExpressionOpRight((Expression) o2, right.getToken()));
                    tryRemoveSpace().acceptColon();
                    return true;
                } else {
                    panic("what is situation ?");
                }
                break;
            case "ExpressionID DOT_DOT_DOT":
                addLast(new ExpressionChangeID((ExpressionID) o2, (Token) o1));
                tryRemoveSpace();
                return true;
            case "MergeOperatorBoth DOT_DOT_DOT":
                Token op = ((MergeOperatorBoth) o2).getToken();
                if (op.is(Kind.COMMA)) {
                    addLast(o2);
                    addLast(new ExpressionChangeID(null, (Token) o1));
                    tryRemoveSpace();
                    return true;
                }
                panic("should be ,");
                break;
            case "MergeOperatorBoth ExpressionTrait":
            case "ExpressionID ExpressionTrait":
            case "ExpressionArray ExpressionTrait":
            case "ExpressionStruct ExpressionTrait":
            case "ExpressionChangeID ExpressionTrait":
            case "MergeOperatorOverload ExpressionTrait":
                addLast(o2);
                addLast(new ExpressionTraits(null, (ExpressionTrait) o1));
                tryRemoveSpace();
                return true;
            case "ExpressionTraits ExpressionTrait":
                addLast(new ExpressionTraits((ExpressionTraits) o2, (ExpressionTrait) o1));
                tryRemoveSpace();
                return true;
            case "ExpressionChangeID ExpressionTraits":
            case "ExpressionID ExpressionTraits":
                if (glance().not(Kind.COLON)) {
                    addLast(new ExpressionWithTraits((Expression) o2, (ExpressionTraits) o1));
                    tryRemoveSpace();
                    return true;
                }
                break;
            case "MergeOperatorOverload ExpressionTraits":
                switch (glance().kind) {
                    case COLON: break;
                    case LB:
                        Expression expression = ExpressionChannel.produce(this, Kind.CR);
                        addLast(new ExpressionOperatorOverload(((MergeOperatorOverload) o2).getToken(), (ExpressionTraits) o1, null, expression));
                        tryRemoveSpace();
                        return true;
                    case ASSIGN_FORCE: break;
                    default: panic("wrong token.", glance());
                }
                break;
            case "LS RS":
                addLast(new ExpressionSquareBracket((Token) o2, new ExpressionEmpty((Token) o2, (Token) o1),(Token) o1));
                return true;
            case "LR RR":
                addLast(new ExpressionRoundBracket((Token) o2, new ExpressionEmpty((Token) o2, (Token) o1),(Token) o1));
                tryRemoveSpace();
                return true;
            case "MergeOperatorOverload ExpressionRoundBracket":
                if (isChannelEmpty()) {
                    addLast(o2);
                    addLast(new ExpressionTrait(null, (Expression) o1));
                    return true;
                }
                break;
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
            if (CONSTANT.contains(token.kind)) {
                addLast(new ExpressionConstant(token));
                tryRemoveSpace();
                return true;
            }
            if (OVERLOAD_OPERATOR.contains(token.kind)) {
                if (isChannelEmpty()) {
                    boolean overload = true;
                    if (LEFT_OPERATOR.contains(token.kind)) {
                        if (glanceSkipSpace().is(Kind.COLON)) {

                        } else if (glance().is(Kind.LR) && token.next(glance())) {
                            // 即使这样还有可能是括号表达式
                            mark();
                            next();
                            Expression expression = ExpressionChannel.produce(this, Kind.RR);
                            if (expression instanceof ExpressionWithTraits) {

                            } else if (expression instanceof ExpressionOpMiddle) {
                                if (((ExpressionOpMiddle) expression).getLeftExpression() instanceof ExpressionWithTraits) {

                                } else {
                                    overload = false;
                                }
                            } else {
                                overload = false;
                            }
                            recover();
                        } else {
                            overload = false;
                        }
                    }
                    if (overload) {
                        addLast(new MergeOperatorOverload(token));
                        removeSpace();
                        return true;
                    }
                }
            }

            if (LEFT_OPERATOR.contains(token.kind)) {
                // + 和 - 需要检查前面的运算符是什么才能归结为左操作符
                boolean change = true;

                if (token.is(Kind.ADD, Kind.SUB) && isChannelFull()) {
                    Object o  = getLast();
                    if (o instanceof Expression) {
                        change = false;
                    } else if (o instanceof Token) {
                        Token t = (Token) o;
                        if (PRIORITY.containsKey(t.kind)) {
                            if (!priority(token, t)) {
                                change = false;
                            }
                        }
                    } else {
                        panic("what is this ?");
                    }
                }
                if (change) {
                    addLast(new MergeOperatorLeft(token));
                    removeSpace();
                    return true;
                }
            }
            if (MIDDLE_OPERATOR.contains(token.kind)) {

                addLast(new MergeOperatorBoth(token));
                ignoreSpace();
                return true;
            }
            if (RIGHT_OPERATOR.contains(token.kind)) {
                addLast(new MergeOperatorRight(token));
                ignoreSpace();
                return true;
            }
            if (SINGLE_KEY_WORDS.contains(token.kind)) {
                addLast(new ExpressionKeyWord(token));
                accept(Kind.DOT).refuseAll();
                return true;
            }
            Expression expression;
            if (ASSIGN_OPERATOR.contains(token.kind)) {removeSpace();
                expression = ExpressionChannel.produce(this, extend(Kind.CR));
                addLast(new MergeAssign(token));
                addLast(expression);
                tryRemoveSpace();
                return true;
            }
            Token next;
            switch (token.kind) {
                case ID:
                    addLast(new ExpressionID((Token) o1));
                    tryRemoveSpace();
                    return true;
                case LB:
                    if (isChannelEmpty()
                            || (channelSize(1) && getFirst() instanceof ExpressionSquareBracket)
                            || (channelSize(1) && getFirst() instanceof ExpressionRoundBracket)
                            || (isChannelFull() && getLast() instanceof MergeOperatorBoth
                                && ((MergeOperatorBoth) getLast()).getToken().is(Kind.LAMBDA)) ) {
                        removeSpaceOrCR();
                        Statements statements = parseStatements(Kind.RB, Kind.COMMA, Kind.CR);
                        removeSpaceOrCR();
                        if (glance().not(Kind.RB)) {
                            panic("must be }.", glance());
                        }
                        addLast(new ExpressionStruct(token, statements, next()));
                        tryRemoveSpace().refuseAll();
                        if (!(isChannelFull() && getLast() instanceof MergeOperatorBoth
                                && ((MergeOperatorBoth) getLast()).getToken().is(Kind.LAMBDA))) {
                            acceptColon();
                        }
                        return true;
                    }
                    break;
                case LS:
                    removeSpace();
                    expression = ExpressionChannel.produce(this, Kind.RS);
                    removeSpace();
                    next = next();
                    if (next.not(Kind.RS)) {
                        panic("must be ]");
                    }
                    addLast(new ExpressionSquareBracket(token, expression, next));
                    tryRemoveSpace();
                    return true;
                case LR:
                    removeSpace();
                    expression = ExpressionChannel.produce(this, Kind.RR);
                    next = next();
                    if (next.not(Kind.RR)) {
                        panic("must be )");
                    }
                    addLast(new ExpressionRoundBracket(token, expression, next));
                    tryRemoveSpace();
                    return true;
                case COLON:
                    // 尝试解析一个对象
                    switch (glance().kind) {
                        case NATIVE:
                        case GOTO:
                        case ENUM:
                            expression = new ExpressionKeyWord(next());break;
                        default:
                            List<Kind> ends = new ArrayList<>(Arrays.asList(Kind.COLON, Kind.COMMA, Kind.ASSIGN_FORCE, Kind.ASSIGN));
                            if (status().startsWith("MergeOperatorOverload")) { ends.add(Kind.LB); }
                            expression = ExpressionChannel.produce(this, extend(ends.toArray(new Kind[]{})));
                    }
                    if (token.next(expression.first())) {
                        addLast(new ExpressionTrait(token, expression));
                        tryRemoveSpace().acceptColon();
                        return true;
                    } else {
                        panic("should be connected. " + token + " " + expression.first());
                    }
                default:
            }
        }

        String status = parseName(o1);
        switch (status) {
            case "ExpressionTrait":
                if (isChannelEmpty()) {
                    addLast(new ExpressionTraits(null, (ExpressionTrait) o1));
                    tryRemoveSpace();
                    return true;
                }
                break;
            case "DOT_DOT_DOT":
                if (isChannelEmpty()) {
                    addLast(new ExpressionChangeID(null, (Token) o1));
                    tryRemoveSpace();
                    return true;
                }
                break;
            default:
        }
        addLast(o1);
        return false;
    }

    @Override
    protected void digested() {
        Token next = glance();
        if (end(next)) {
            boolean full = true;
            if (!end(Kind.COLON) && next.is(Kind.COLON)) {
                full = false;
            }
            if (full) {
                this.full();
            }
        }
    }

    private void full() {
        int size = channelSize();
        if (0 < size) {
            if (getFirst() instanceof Expression || getFirst() instanceof MergeOperatorOverload) {
                if (1 == size) {
                    data = (Expression) removeFirst();
                    return;
                }
                String status = status();
                switch (status) {
                    case "ExpressionArray ExpressionTraits":
                    case "ExpressionStruct ExpressionTraits":
                        data = new ExpressionWithTraits((Expression) removeFirst(), (ExpressionTraits) removeFirst());
                        return;
                    case "ExpressionWithTraits MergeAssign ExpressionFunction":
                    case "ExpressionWithTraits MergeAssign ExpressionLambda":
                        ExpressionWithTraits withTraits = (ExpressionWithTraits) removeFirst();
                        data = new ExpressionWithTraitsAssign(withTraits.getExpressionID(), withTraits.getTraits(), ((MergeAssign) removeFirst()).getToken(), (Expression) removeFirst());
                        return;
                    case "ExpressionOpMiddle MergeAssign ExpressionFunctionCall":
                        data = new ExpressionAssign((Expression) removeFirst(), ((MergeAssign) removeFirst()).getToken(), (Expression) removeFirst());
                        return;

                    case "MergeOperatorOverload ExpressionTraits MergeAssign ExpressionFunction":
                        data = new ExpressionOperatorOverload(((MergeOperatorOverload) removeFirst()).getToken(),
                                (ExpressionTraits) removeFirst(), ((MergeAssign) removeFirst()).getToken(),
                                (Expression) removeFirst());
                        return;
                    default:
                        panic("what is status ?");
                }
            }
        }
        panic("should not end.");
    }

    public static Expression produce(Channel channel, Kind... end) {
        return new ExpressionChannel(channel, end).produce();
    }

}
