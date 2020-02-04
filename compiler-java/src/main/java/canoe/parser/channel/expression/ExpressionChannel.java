package canoe.parser.channel.expression;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.expression.Expression;
import canoe.parser.syntax.expression.ExpressionEmpty;
import canoe.parser.syntax.expression.call.ExpressionFunctionCall;
import canoe.parser.syntax.expression.pair.ExpressionRound;
import canoe.parser.syntax.expression.single.ExpressionID;
import canoe.parser.syntax.expression.split.ExpressionDot;
import canoe.parser.syntax.merge.MergeOperatorLeft;

import java.util.concurrent.ConcurrentLinkedDeque;

import static canoe.lexer.KindSet.KEY_WORDS;
import static canoe.parser.syntax.expression.single.ExpressionConstant.CONSTANT;

/**
 * @author dawn
 */
public class ExpressionChannel extends Channel<Expression> {

    private ConcurrentLinkedDeque<Token> pair = new ConcurrentLinkedDeque<>();

    private ExpressionChannel(Channel channel, Kind... end) {
        super(channel, end);
        Token next = glance();
        if (!KEY_WORDS.contains(next.kind)
                && !MergeOperatorLeft.OPERATOR_LEFT.contains(next.kind)
                && !CONSTANT.contains(next.kind)) {
            if (over(next)) { panic("can not start with " + next.kind.value, next); }
            switch (next.kind) {
                case LB: case RB:
                case LR: case RR:
                case LS: case RS:
                case ID: case COLON:
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
//            case IF: data = new ExpressionIf(IfChannel.make(this, extend())); dropSpaces(); return false;

//            case MATCH: data = new ExpressionMatch(MatchChannel.produce(this, extend())); return false;
//            case RR: case RB: case RS:
//                data = new ExpressionEmpty(current(), next); return false;
            default:
        }

        // 检查关键词
//        if (SINGLE_KEY_WORDS.contains(next.kind) || next.kind == Kind.ELSE_IF) {
//            boolean pass = false;
//            if (CONSTANT.contains(next.kind)) { pass = true; }
//            if (isChannelFull()) {
//                Object o = getLast();
//                if (o instanceof Token) {
//                    switch (((Token) o).kind) {
//                        case DOT:  pass = true; break;
//                        case COLON:
//                            switch (next.kind) {
//                                case NATIVE:
//                                case GOTO:
//                                    pass = true;
//                                    break;
//                                default:
//                            }
//                            break;
//                        default:
//                    }
//                } else if (o instanceof MergeOperatorMiddle && ((MergeOperatorMiddle) o).getToken().is(Kind.DOT)) {
//                    pass = true;
//                }
//            }
//            if (isChannelEmpty()) {
//                mark();
//                next();
//                if (glance().is(Kind.DOT)) { pass = true; }
//                recover();
//            }
//            if (!pass) {
//                panic("should be here.", next);
//            }
//        }
        if (over(next) && channelFull()) {
            panic("should be here.", next);
        }
        switch (next.kind) {
            case LB: case LR: case LS: pair.add(next); break;
            case RB: case RR: case RS:
                Token last = pair.getLast();
                if (next.kind.name().charAt(1) == last.kind.name().charAt(1)) {
                    pair.removeLast();
                } else {
                    panic("bracket must be paired", next);
                }
                break;
            default:
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
//            case "ExpressionConstant":
//            case "ExpressionOpMiddle":
//            case "ExpressionComma":
            case "ExpressionDot":
            case "ExpressionID":
//            case "ExpressionWithTraits":
//            case "ExpressionArrayCall":
//            case "ExpressionOpLeft":
//            case "ExpressionRound":
//            case "ExpressionFunctionCall":
//            case "ExpressionStruct":
//
//            case "MergeOperatorLeft":
//            case "LR":
//
//
//            case "ExpressionConstant MergeOperatorMiddle":
//            case "ExpressionOpMiddle MergeOperatorMiddle":
//            case "ExpressionID MergeOperatorMiddle":
//
//            case "ExpressionConstant COMMA":
//
//            case "ExpressionConstant DOT":
            case "ExpressionID DOT":
            case "ExpressionDot DOT":

//            case "ExpressionConstant LS":
//            case "ExpressionID LR":
//
//            case "LR ExpressionID":
//            case "LR ExpressionOpMiddle":
//
//
//            case "ExpressionConstant MergeOperatorMiddle ExpressionConstant":
//            case "ExpressionConstant DOT ExpressionID":
            case "ExpressionID DOT ExpressionID":
            case "ExpressionID DOT ExpressionFunctionCall":
            case "ExpressionDot DOT ExpressionID":

//            case "ExpressionWithTraits MergeAssign ExpressionConstant":
//
//            case "ExpressionConstant LS ExpressionConstant":
//            case "ExpressionID LR ExpressionID":
//            case "ExpressionID LR ExpressionOpMiddle":
//
//            case "ExpressionID DOT MergeOperatorOverload":
//
//            case "LR ExpressionID MergeOperatorMiddle":
//
//
//            case "ExpressionConstant MergeOperatorMiddle ExpressionConstant MergeOperatorMiddle":
//            case "ExpressionConstant DOT ExpressionID LR":
//
//            case "ExpressionID DOT MergeOperatorOverload LR":
            case "ExpressionID DOT ExpressionID LR":
            case "ExpressionDot DOT ExpressionID LR":

//            case "ExpressionID LR ExpressionID MergeOperatorMiddle":
//            case "ExpressionID LR ExpressionOpMiddle MergeOperatorMiddle":
//
//
//
//            case "ExpressionConstant DOT ExpressionID LR ExpressionConstant":
//            case "ExpressionID DOT MergeOperatorOverload LR ExpressionID":
            case "ExpressionID DOT ExpressionID LR ExpressionID":

            case "ExpressionID DOT ExpressionID LR ExpressionDot":

            case "ExpressionID DOT ExpressionFunctionCall DOT ExpressionID":

//            case "ExpressionID LR ExpressionOpMiddle MergeOperatorMiddle ExpressionID":

//            case "ExpressionID LR ExpressionOpMiddle MergeOperatorMiddle ExpressionID MergeOperatorMiddle":

            case "ExpressionID DOT ExpressionID LR ExpressionID DOT":

            case "ExpressionID DOT ExpressionID LR ExpressionID DOT ExpressionID":

            case "ExpressionID DOT ExpressionID LR ExpressionID DOT ExpressionID LR":

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
//            case "ExpressionConstant MergeOperatorMiddle ExpressionConstant":
//            case "ExpressionConstant MergeOperatorMiddle ExpressionOpMiddle":
//            case "ExpressionID MergeOperatorMiddle ExpressionID":
//            case "ExpressionID MergeOperatorMiddle ExpressionConstant":
//            case "ExpressionOpMiddle MergeOperatorMiddle ExpressionConstant":
//            case "ExpressionOpMiddle MergeOperatorMiddle ExpressionID":
//            case "ExpressionOpMiddle MergeOperatorMiddle ExpressionOpMiddle":
//                // 检查运算符优先级
//                op = ((MergeOperatorMiddle) o2).getToken();
//                if (priority(op, glanceSkipSpaces())) {
////                    if (op.is(Kind.LAMBDA)) {
////                        // lambda 要特别对待
////                        addLast(new ExpressionLambda((Expression) o3, op, (Expression) o1));
////                    } else if (op.is(Kind.DOT) && o1 instanceof ExpressionRound) {
////                        addLast(new ExpressionCast((Expression) o3, op, (ExpressionRound) o1));
////                    } else {
//                        addLast(new ExpressionOpMiddle((Expression) o3, op, (Expression) o1));
////                    }
//                    dropSpaces().refuseAll();
//                    return true;
//                }
//                break;
//            case "ExpressionConstant COMMA ExpressionConstant":
//                // 检查运算符优先级
//                op = (Token) o2;
//                if (priority(op, glanceSkipSpaces())) {
//                    addLast(new ExpressionComma((Expression) o3, op, (Expression) o1));
//                    dropSpaces().refuseAll();
//                    return true;
//                }
//                break;
//            case "ExpressionID DOT ExpressionOverloadCall":
            case "ExpressionID DOT ExpressionFunctionCall":
            case "ExpressionID DOT ExpressionID":
            case "ExpressionDot DOT ExpressionID":
            case "ExpressionDot DOT ExpressionFunctionCall":
                // 检查运算符优先级
                op = (Token) o2;
                if (priority(op, glanceSkipSpaces())) {
                    addLast(new ExpressionDot((Expression) o3, op, (Expression) o1));
                    tryRemoveSpacesOrCR().tryAcceptPair().refuseAll();
                    return true;
                }
                break;
////            case "ExpressionTraits MergeOperatorMiddle ExpressionTraits":
////            case "ExpressionTraits MergeOperatorMiddle ExpressionWithTraits":
////                // 检查运算符优先级
////                op = ((MergeOperatorMiddle) o2).getToken();
////                if (op.is(Kind.COMMA) && priority(op, glanceSkipSpace())) {
////                    addLast(new ExpressionCommaTraits((ExpressionTraits) o3, op, (Expression) o1));
////                    return true;
////                }
////                break;
//            case "LR ExpressionOpMiddle RR":
//            case "LR ExpressionID RR":
//            case "LR ExpressionConstant RR":
            case "LR ExpressionDot RR":
                addLast(new ExpressionRound((Token) o3, (Expression) o2, (Token) o1));
                dropSpaces().refuseAll();
                return true;
//            case "LS ExpressionConstant RS":
//                addLast(new ExpressionSquare((Token) o3, (Expression) o2, (Token) o1));
//                dropSpaces().refuseAll();
//                return true;

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
        Expression expression;
        switch (status) {
//            case "ExpressionOpMiddle MergeOperatorRight":
//                addLast(new ExpressionOpRight((Expression) o2, ((MergeOperatorRight) o1).getToken()));
//                tryRemoveSpace();
//                return true;
            case "ExpressionID ExpressionRound":
                addLast(new ExpressionFunctionCall((ExpressionID) o2, (ExpressionRound) o1));
                tryRemoveSpacesOrCR().tryAcceptPair()
                        .accept(Kind.DOT)
                        .refuseAll();
                return true;
//            case "MergeOperatorOverload ExpressionRound":
//                addLast(new ExpressionOverloadCall(((MergeOperatorOverload) o2).getToken(), (ExpressionRound) o1));
//                dropSpaces().refuseAll();
//                return true;
//            case "ExpressionConstant ExpressionSquare":
//                addLast(new ExpressionArrayCall((Expression) o2, (ExpressionSquare) o1));
//                dropSpaces().refuseAll();
//                return true;
//            case "ExpressionID COLON":
//                // 尝试解析一个对象
//                Token colon = (Token) o1;
//                switch (glance().kind) {
//                    case NATIVE:
//                    case GOTO:
//                    case ENUM:
//                        expression = new ExpressionKeyWord(next());break;
//                    default:
//                        List<Kind> ends = new ArrayList<>(Arrays.asList(Kind.COLON, Kind.COMMA));
//                        ends.addAll(OPERATOR_ASSIGN);
//                        expression = ExpressionChannel.produce(this, extend(ends.toArray(new Kind[]{})));
//                }
//                if (colon.next(expression.first())) {
//                    addLast(o2);
//                    addLast(new ExpressionTraits(null, colon, expression));
//                    dropSpaces().refuseAll();
//                    return true;
//                } else {
//                    panic("should be connected. " + colon + " " + expression.first());
//                }
//                break;
//            case "ExpressionID ExpressionTraits":
//                addLast(new ExpressionWithTraits((Expression) o2, (ExpressionTraits) o1));
//                dropSpaces()
//                        .accept(OPERATOR_ASSIGN)
//                        .refuseAll();
//                return true;
////            case "ExpressionRound ExpressionStruct":
////                addLast(new ExpressionFunction((ExpressionRound) o2, (ExpressionStruct) o1));
////                tryRemoveSpace();
////                return true;
////            case "ExpressionSquare ExpressionStruct":
////                ExpressionStruct struct = (ExpressionStruct) o1;
////                ExpressionSquare squareBracket = (ExpressionSquare) o2;
////                if (!squareBracket.last().next(struct.first())) {
////                    panic("should be connected. " + squareBracket.last() + " " + struct.first());
////                }
////                addLast(new ExpressionArray(squareBracket, struct));
////                tryRemoveSpace().acceptColon().refuseAll();
////                return true;
//            case "MergeOperatorLeft ExpressionConstant":
////            case "MergeOperatorLeft ExpressionRound":
////            case "MergeOperatorLeft ExpressionID":
//                MergeOperatorLeft left = (MergeOperatorLeft) o2;
//                String leftOtherSign = left.getToken().is(Kind.ADD, Kind.SUB) ? "l" : null;
//                if (priority(left.getToken(), glanceSkipSpaces(), leftOtherSign)) {
//                    addLast(new ExpressionOpLeft(left.getToken(), (Expression) o1));
//                    dropSpaces().refuseAll();
//                    return true;
//                }
//                break;
////            case "ExpressionOpRight MergeOperatorRight":
////            case "ExpressionID MergeOperatorRight":
////                MergeOperatorRight right = (MergeOperatorRight) o1;
////                if (priority(right.getToken(), glanceSkipSpace())) {
////                    addLast(new ExpressionOpRight((Expression) o2, right.getToken()));
////                    tryRemoveSpace().acceptColon();
////                    return true;
////                } else {
////                    panic("what is situation ?");
////                }
////                break;
////            case "ExpressionID DOT_DOT_DOT":
////                addLast(new ExpressionChangeID((ExpressionID) o2, (Token) o1));
////                tryRemoveSpace();
////                return true;
////            case "MergeOperatorMiddle DOT_DOT_DOT":
////                Token op = ((MergeOperatorMiddle) o2).getToken();
////                if (op.is(Kind.COMMA)) {
////                    addLast(o2);
////                    addLast(new ExpressionChangeID(null, (Token) o1));
////                    tryRemoveSpace();
////                    return true;
////                }
////                panic("should be ,");
////                break;
//
////            case "ExpressionChangeID ExpressionTraits":
//
////            case "MergeOperatorOverload ExpressionTraits":
////                switch (glance().kind) {
////                    case COLON: break;
////                    case LB:
////                        Expression expression = ExpressionChannel.produce(this, Kind.CR);
////                        addLast(new ExpressionOperatorOverload(((MergeOperatorOverload) o2).getToken(), (ExpressionTraits) o1, null, expression));
////                        tryRemoveSpace();
////                        return true;
////                    case ASSIGN_FORCE: break;
////                    default: panic("wrong token.", glance());
////                }
////                break;
////            case "LS RS":
////                addLast(new ExpressionSquare((Token) o2, new ExpressionEmpty((Token) o2, (Token) o1),(Token) o1));
////                return true;
            case "LR RR":
                addLast(new ExpressionRound((Token) o2, new ExpressionEmpty((Token) o2, (Token) o1),(Token) o1));
                dropSpaces().refuseAll();
                return true;
////            case "MergeOperatorOverload ExpressionRound":
////                if (isChannelEmpty()) {
////                    addLast(o2);
////                    addLast(new ExpressionTrait(null, (Expression) o1));
////                    return true;
////                }
////                break;
            default:
        }
        addLast(o2);
        addLast(o1);
        return false;
    }

    private boolean digest1() {
        if (channelEmpty()) { return false; }
        Object o1 = removeLast();

        if (o1 instanceof Token) {
            Token token = (Token) o1;
//            if (CONSTANT.contains(token.kind)) {
//                addLast(new ExpressionConstant(token));
//                tryRemoveSpacesOrCR().tryAcceptPair()
//                        .accept(OPERATOR_MIDDLE)
//                        .accept(Kind.COMMA, Kind.DOT)
//                        .accept(Kind.LS)
//                        .refuseAll();
//                return true;
//            }
//            if (MergeOperatorOverload.OPERATOR_OVERLOAD.contains(token.kind)) {
//                if (channelFull()) {
//                    if (getLast() instanceof Token && ((Token) getLast()).is(Kind.DOT)) {
//                        addLast(new MergeOperatorOverload(token));
//                        dropSpaces()
//                            .accept(Kind.LR)
//                            .refuseAll();
//                        return true;
//                    }
//                }
////                if (isChannelEmpty()) {
////                    boolean overload = true;
////                    if (LEFT_OPERATOR.contains(token.kind)) {
////                        if (glanceSkipSpace().is(Kind.COLON)) {
////
////                        } else if (glance().is(Kind.LR) && token.next(glance())) {
////                            // 即使这样还有可能是括号表达式
////                            mark();
////                            next();
////                            Expression expression = ExpressionChannel.produce(this, Kind.RR);
////                            if (expression instanceof ExpressionWithTraits) {
////
////                            } else if (expression instanceof ExpressionOpMiddle) {
////                                if (((ExpressionOpMiddle) expression).getLeftExpression() instanceof ExpressionWithTraits) {
////
////                                } else {
////                                    overload = false;
////                                }
////                            } else {
////                                overload = false;
////                            }
////                            recover();
////                        } else {
////                            overload = false;
////                        }
////                    }
////                    if (overload) {
////                        addLast(new MergeOperatorOverload(token));
////                        removeSpace();
////                        return true;
////                    }
////                }
//            }

//            if (MergeOperatorLeft.OPERATOR_LEFT.contains(token.kind)) {
//                // + 和 - 需要检查前面的运算符是什么才能归结为左操作符
//                boolean change = true;
//
//                if (token.is(Kind.ADD, Kind.SUB) && channelFull()) {
//                    Object o  = getLast();
//                    if (o instanceof Expression) {
//                        change = false;
//                    } else if (o instanceof Token) {
//                        Token t = (Token) o;
//                        if (PRIORITY.containsKey(t.kind)) {
//                            if (!priority(token, t)) {
//                                change = false;
//                            }
//                        }
//                    } else {
//                        panic("what is this ?");
//                    }
//                }
//                if (change) {
//                    addLast(new MergeOperatorLeft(token));
//                    dropSpaces();
//                    return true;
//                }
//            }
//            if (MergeOperatorMiddle.OPERATOR_MIDDLE.contains(token.kind)) {
//                addLast(new MergeOperatorMiddle(token));
//                dropSpacesCR()
//                        .accept(CONSTANT)
//                        .accept(Kind.ID)
//                        .refuseAll();
//                return true;
//            }
//            Expression expression;
//            if (MergeAssign.OPERATOR_ASSIGN.contains(token.kind)) { dropSpaces();
//                expression = ExpressionChannel.produce(this, extend(Kind.CR));
//                addLast(new MergeAssign(token));
//                addLast(expression);
//                dropSpaces().refuseAll();
//                return true;
//            }
//            if (RIGHT_OPERATOR.contains(token.kind)) {
//                addLast(new MergeOperatorRight(token));
//                ignoreSpace();
//                return true;
//            }
//            if (SINGLE_KEY_WORDS.contains(token.kind)) {
//                addLast(new ExpressionKeyWord(token));
//                accept(Kind.DOT).refuseAll();
//                return true;
//            }

//            Token next;
            switch (token.kind) {
//                case LR: dropSpacesCR()
//                        .accept(Kind.RR)
//                        .accept(CONSTANT)
//                        .accept(Kind.ID)
//                        .refuseAll();
//                    break;
//                case DOT: dropSpacesCR()
//                        .accept(Kind.ID)
//                        .accept(OPERATOR_OVERLOAD)
//                        .refuseAll();
//                    break;
                case ID: addLast(new ExpressionID((Token) o1));
                    tryRemoveSpacesOrCR().tryAcceptPair()
//                            .accept(OPERATOR_MIDDLE)
                            .accept(Kind.DOT)
                            .accept(Kind.LR)
//                            .accept(Kind.COLON)
                            .refuseAll();
                    return true;
//                case LB:
//                    if (channelEmpty()
////                            || (channelSize(1) && getFirst() instanceof ExpressionSquare)
////                            || (channelSize(1) && getFirst() instanceof ExpressionRound)
////                            || (isChannelFull() && getLast() instanceof MergeOperatorMiddle
////                                && ((MergeOperatorMiddle) getLast()).getToken().is(Kind.LAMBDA))
//                                ) {
//                        dropSpacesCR();
//                        Statements statements = parseStatements(Kind.RB, Kind.COMMA, Kind.CR, Kind.SEMI);
//                        dropSpacesCR();
//                        if (glance().not(Kind.RB)) {
//                            panic("must be }", glance());
//                        }
//                        addLast(new ExpressionStruct(token, statements, next()));
//                        dropSpaces().refuseAll();
////                        if (!(isChannelFull() && getLast() instanceof MergeOperatorMiddle
////                                && ((MergeOperatorMiddle) getLast()).getToken().is(Kind.LAMBDA))) {
////                            acceptColon();
////                        }
//                        return true;
//                    }
//                    break;
//                case LS:
//                    removeSpace();
//                    expression = ExpressionChannel.produce(this, Kind.RS);
//                    removeSpace();
//                    next = next();
//                    if (next.not(Kind.RS)) {
//                        panic("must be ]");
//                    }
//                    addLast(new ExpressionSquare(token, expression, next));
//                    tryRemoveSpace();
//                    return true;
//                case LR:
//                    removeSpace();
//                    expression = ExpressionChannel.produce(this, Kind.RR);
//                    next = next();
//                    if (next.not(Kind.RR)) {
//                        panic("must be )");
//                    }
//                    addLast(new ExpressionRound(token, expression, next));
//                    tryRemoveSpace();
//                    return true;
                default:
            }
        }

        String status = parseName(o1);
        switch (status) {
//            case "ExpressionTrait":
//                if (isChannelEmpty()) {
//                    addLast(new ExpressionTraits(null, (ExpressionTrait) o1));
//                    tryRemoveSpace();
//                    return true;
//                }
//                break;
//            case "DOT_DOT_DOT":
//                if (isChannelEmpty()) {
//                    addLast(new ExpressionChangeID(null, (Token) o1));
//                    tryRemoveSpace();
//                    return true;
//                }
//                break;
            default:
        }
        addLast(o1);
        return false;
    }

    private ExpressionChannel tryRemoveSpacesOrCR() {
        if (pair.isEmpty()) {
            dropSpaces();
        } else {
            dropSpacesCR();
        }
        return this;
    }
    private ExpressionChannel tryAcceptPair() {
        if (!pair.isEmpty()) {
            switch (pair.getLast().kind) {
                case LB: accept(Kind.RB); break;
                case LR: accept(Kind.RR); break;
                case LS: accept(Kind.RS); break;
                default:
            }
        }
        return this;
    }


    @Override
    protected void digested() {
        Token next = glance();
        if (over(next)) {
            boolean full = true;
            if (!over(Kind.COLON) && next.is(Kind.COLON)) {
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
            if (getFirst() instanceof Expression
                    //|| getFirst() instanceof MergeOperatorOverload
            ) {
                if (1 == size) {
                    data = (Expression) removeFirst();
                    return;
                }
                String status = status();
                switch (status) {
//                    case "ExpressionArray ExpressionTraits":
//                    case "ExpressionStruct ExpressionTraits":
//                        data = new ExpressionWithTraits((Expression) removeFirst(), (ExpressionTraits) removeFirst());
//                        return;
//                    case "ExpressionWithTraits MergeAssign ExpressionConstant":
//                    case "ExpressionWithTraits MergeAssign ExpressionFunction":
//                    case "ExpressionWithTraits MergeAssign ExpressionLambda":
//                        ExpressionWithTraits withTraits = (ExpressionWithTraits) removeFirst();
//                        data = new ExpressionWithTraitsAssign(withTraits.getId(), withTraits.getTraits(), ((MergeAssign) removeFirst()).getToken(), (Expression) removeFirst());
//                        return;
//                    case "ExpressionOpMiddle MergeAssign ExpressionFunctionCall":
//                        data = new ExpressionAssign((Expression) removeFirst(), ((MergeAssign) removeFirst()).getToken(), (Expression) removeFirst());
//                        return;
//
//                    case "MergeOperatorOverload ExpressionTraits MergeAssign ExpressionFunction":
//                        data = new ExpressionOperatorOverload(((MergeOperatorOverload) removeFirst()).getToken(),
//                                (ExpressionTraits) removeFirst(), ((MergeAssign) removeFirst()).getToken(),
//                                (Expression) removeFirst());
//                        return;
                    default: panic("what is status ?");
                }
            }
        }
        panic("should not end.");
    }

    public static Expression produce(Channel channel, Kind... end) {
        return new ExpressionChannel(channel, end).make();
    }

}
