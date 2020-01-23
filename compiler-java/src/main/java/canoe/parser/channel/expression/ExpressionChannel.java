package canoe.parser.channel.expression;

import canoe.ast.expression.*;
import canoe.ast.merge.MergeOperatorBoth;
import canoe.ast.merge.MergeOperatorLeft;
import canoe.ast.merge.MergeOperatorRight;
import canoe.ast.statement.Statements;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.channel.statement.condition.IfChannel;
import canoe.parser.channel.statement.condition.MatchChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static canoe.lexer.KindSet.*;

/**
 * @author dawn
 */
public class ExpressionChannel extends Channel<Expression> {

    private static HashMap<String, Integer> PRIORITY = new HashMap<>(97);

    static {
        // ( ) [ ] -> 后缀运算符 从左到右
        PRIORITY.put("(", 1);
//        PRIORITY.put(")", 1);
        PRIORITY.put("[", 1);
//        PRIORITY.put("]", 1);

        PRIORITY.put(".", 2);
        PRIORITY.put("..", 2);
        // ! + - ++ -- 单目运算符 从右到左
        PRIORITY.put("!", 3);
        PRIORITY.put("+l", 3);
        PRIORITY.put("-l", 3);
        PRIORITY.put("++", 3);
        PRIORITY.put("--", 3);
        // * / % 双目运算符 从左到右
        PRIORITY.put("*", 4);
        PRIORITY.put("/", 4);
        PRIORITY.put("%", 4);
        // + - 双目运算符 从左到右
        PRIORITY.put("+", 5);
        PRIORITY.put("-", 5);
        // >> << 位移运算符 双目 从左到右
        PRIORITY.put(">>", 6);
        PRIORITY.put("<<", 6);
        // < <= > >= 关系运算符 双目 从左到右
        PRIORITY.put(">", 7);
        PRIORITY.put(">=", 7);
        PRIORITY.put("<", 7);
        PRIORITY.put("<=", 7);

        PRIORITY.put("<-", 7);
        // == != 关系运算符 双目 从左到右
        PRIORITY.put("==", 8);
        PRIORITY.put("!=", 8);
        // & 按位与 双目 从左到右
        PRIORITY.put("&", 9);
        // & 按位异或 双目 从左到右
        PRIORITY.put("^", 10);
        // & 按位或 双目 从左到右
        PRIORITY.put("|", 11);
        // & 逻辑与 双目 从左到右
        PRIORITY.put("&&", 12);
        // & 逻辑或 双目 从左到右
        PRIORITY.put("||", 13);
        // := = += -= /= %= >>= <<= &= |= 赋值运算 双目 从右到左
        PRIORITY.put(":=", 14);
        PRIORITY.put("=", 14);
        PRIORITY.put("+=", 14);
        PRIORITY.put("-=", 14);
        PRIORITY.put("*=", 14);
        PRIORITY.put("/=", 14);
        PRIORITY.put("%=", 14);
        PRIORITY.put(">>=", 14);
        PRIORITY.put("<<=", 14);
        PRIORITY.put("&=", 14);
        PRIORITY.put("^=", 14);
        PRIORITY.put("|=", 14);
        // , 运算 双目 从左到右
        PRIORITY.put(",", 15);

        // lambda表达式的优先级应该很低
        PRIORITY.put("->", 16);

    }

//    private Stack<Token> pairSign = new Stack<>();

    private ExpressionChannel(Channel channel, Kind... end) {
        super(channel, end);
        Token next = glance();
        if (!SINGLE_KEY_WORDS.contains(next.kind)
                && !LEFT_OPERATOR.contains(next.kind)
                && !CONSTANT.contains(next.kind)) {
//            if (end(next)) { panic("can not start with " + next.kind.sign, next); }
            switch (next.kind) {
                case LB:
                case LR:
                case LS:
                case ID:
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

            default:
        }
        // 检查关键词
        if (SINGLE_KEY_WORDS.contains(next.kind) || next.kind == Kind.ELSE_IF) {
            if (!CONSTANT.contains(next.kind)) {
                panic("should be here.", next);
            }
        }
        if (end(next) && isChannelFull()) {
            panic("should be here.", next);
        }
        return true;
    }


//    private void eat() {
//        Token next = glance();
//
//        if (eatSpaceOrCR(next)) { next(); eat(); return; }
//
//        // 遇到左花括号 { 谨慎通过
////        if (next.getKind() == Kind.LB) {
////            if (!objects.isEmpty()) {
////                java.lang.Object o = objects.getLast();
////                if (o instanceof Token) {
////                    Token last = (Token) o;
////                    switch (last.getKind()) {
////                        case RS:
////                        case LAMBDA: break;
////                        // 不允许继续找表达式了
////                        default: return true;
////                    }
////
////                } else {
////                    // 上一个不是 token 就不允许继续找表达式了
////                    return true;
////                }
////            }
////        }
//
////        Token top;
////        switch (next.getKind()) {
////            // [ 和 {
////            case LS: case LB: stack.add(next); break;
////            // ] 和 }
////            case RS: case RB: if (stack.empty()) { return true; }
////                top = stack.pop();
////                if (top.getKind() == Kind.LS) {
////                    if (next.getKind() == Kind.RS) { break; } else { panicToken("try find ] match with: " + top, next); }
////                } else if (top.getKind() == Kind.LB) {
////                    if (next.getKind() == Kind.RB) { break; } else { panicToken("try find } match with: " + top, next); }
////                } else { panicToken("token can not be.", next); }
////                return true;
////
////            case IF:
////                StatementIf statementIf = parseStatementIf.get();
////                objects.addLast(new ExpressionIf(statementIf));
////                return done();
////            case MATCH:
////                StatementMatch statementMatch = parseStatementMatch.get();
////                objects.addLast(new ExpressionMatch(statementMatch));
////                return done();
////
////            case CANOE:
////            case DOT:
////
////            case TRUE:
////            case FALSE:
////            case NUMBER_HEXADECIMAL:
////            case NUMBER_DECIMAL:
////            case NUMBER_OCTAL:
////            case NUMBER_BINARY:
////            case REAL_DECIMAL:
////            case STRING:
////            case BIT_NOT:
////            case ID: break;
////
////            case LR: stack.add(next); break;
////            case RR:
////                if (!stack.empty()) {
////                    top = stack.pop();
////                    if (top.getKind() == Kind.LR) { break; } else { panicToken("try find ) match with: " + top, next); }
////                } else {
////                    return true;
////                }
////
////            case EQ: case NE: case GT: case GE: case LT: case LE:
////            case ADD: case SUB:  case MUL: case DIV: case MOD:
////            case ADD_ADD: case SUB_SUB:
////            case LAMBDA:
////            case COMMA:
////                break;
////
////            case SPACES: reader.nextToken(); return done();
////
////            case IN: return true;
////
////            case CR: if (objects.size() == 1 && objects.getLast() instanceof Expression) { return true; }
////            case COLON: if (objects.size() == 1 && objects.getLast() instanceof Expression) { return true; }
////
////            default: panicToken("can not be this kind of token.", next);
////        }
//
//        if (!CONSTANT.contains(next.kind)
//                && !BINARY_OPERATOR.contains(next.kind)
//                && !RIGHT_OPERATOR.contains(next.kind)) {
//            switch (next.kind) {
//                case MATCH: channel.addLast(new ExpressionMatch(
//                        new MatchChannel(getName(), getStream()).get())); return;
//                case IF: channel.addLast(new ExpressionIf(
//                        new IfChannel(getName(), getStream()).get())); return;
//                case ID:
//
//                case LR: case RR:
//
//                case COMMA:
//                    break;
//                default: panic("can not be this kind of token.", next);
//            }
//        }
//        channel.addLast(next());
//        reduce();
//    }

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
            case "ExpressionFunction":
            case "ExpressionStruct":
            case "ExpressionSquareBracket":
            case "ExpressionRoundBracket":
            case "ExpressionArray":
            case "ExpressionLambda":
                break;
            // 单个运算符
            case "LS":
            case "LR":
            case "MergeOperatorLeft":
                break;

            // 表达式 运算符
            case "ExpressionID MergeOperatorBoth":
            case "ExpressionOpMiddle MergeOperatorBoth":
            case "ExpressionConstant MergeOperatorBoth":
            case "ExpressionOpMiddle LR":
            case "ExpressionID LR":
            case "ExpressionOpMiddle ExpressionRoundBracket":
            case "ExpressionSquareBracket LB":
                break;

            // 表达式 约束
            case "ExpressionStruct ExpressionTrait":
            case "ExpressionArray ExpressionTrait":
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
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID":
                break;

            // 表达式 运算符 表达式 运算符
            case "ExpressionID MergeOperatorBoth ExpressionID LR":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID LR":
            case "ExpressionID MergeOperatorBoth ExpressionID MergeOperatorBoth":
                break;

            // 表达式 运算符 表达式 运算符 表达式
            case "ExpressionID MergeOperatorBoth ExpressionID LR ExpressionConstant":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID LR ExpressionConstant":
            case "ExpressionID MergeOperatorBoth ExpressionID LR ExpressionOpMiddle":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID LR ExpressionOpMiddle":
                break;

            // 表达式 运算符 表达式 运算符 表达式 运算符
            case "ExpressionID MergeOperatorBoth ExpressionID LR ExpressionConstant MergeOperatorBoth":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID LR ExpressionConstant MergeOperatorBoth":
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
//        Token next;
        switch (status) {
            case "ExpressionID MergeOperatorBoth ExpressionID":
            case "ExpressionID MergeOperatorBoth ExpressionConstant":
            case "ExpressionConstant MergeOperatorBoth ExpressionConstant":
            case "ExpressionID MergeOperatorBoth ExpressionFunction":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionFunction":
            case "ExpressionID MergeOperatorBoth ExpressionOpMiddle":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionStruct":
                // 检查运算符优先级
                op = ((MergeOperatorBoth) o2).getToken();
                if (priority(op, glanceSkipSpace())) {
                    if (op.is(Kind.LAMBDA)) {
                        // lambda 要特别对待
                        addLast(new ExpressionLambda((Expression) o3, op, (Expression) o1));
                    } else {
                        addLast(new ExpressionOpMiddle((Expression) o3, op, (Expression) o1));
                    }
                    return true;
                }
                break;
            case "LR ExpressionOpMiddle RR":
            case "LR ExpressionConstant RR":
                addLast(new ExpressionRoundBracket((Token) o3, (Expression) o2, (Token) o1));
                return true;
            case "LB Statements RB":

                return true;
////            case "ExpressionID ADD ExpressionID":
////            case "ExpressionID ADD ExpressionNumber":
////            case "ExpressionNumber ADD ExpressionNumber":
////            case "ExpressionID GT ExpressionNumber":
////                // 检查运算符优先级
////                Token op = (Token) o2;
////                if (priority3(op)) {
////                    objects.addLast(new ExpressionMiddleOp((Expression) o3, op, (Expression) o1));
////                    return true;
////                }
////                break;
////            case "ExpressionID LAMBDA ExpressionMiddleOp":
////            case "ExpressionID LAMBDA ExpressionID":
////                next = nextTokenSkipSpaces();
////                if (next.getKind() == Kind.CR) {
////                    objects.addLast(new ExpressionLambdaExpression((Expression) o3, (Token) o2, (Expression) o1));
////                    return true;
////                }
////                break;
////            case "ExpressionID COMMA ExpressionID":
////                next = nextTokenSkipSpaces();
////                switch (next.getKind()) {
////                    case LAMBDA:
////                    case IN:
////                        objects.addLast(new ExpressionComma((Expression) o3, (Token) o2, (Expression) o1));
////                        return true;
////
////                    default:
////                }
////                break;
////            case "ExpressionComma LAMBDA LB":
////                Statements statements = parseStatements.get();
////                removeSpaceOrCR();
////                next = reader.nextToken();
////                if (next.getKind() != Kind.RB) {
////                    panicToken("can not be.", next);
////                }
////                objects.addLast(new ExpressionLambdaStatements((Expression) o3, (Token) o2, (Token) o1, statements, next));
////                return true;
////            case "ExpressionDotID DOT ExpressionID":
////            case "ExpressionID DOT ExpressionID":
////                next = nextTokenSkipSpaces();
////                if (next.getKind() != Kind.LR) {
////                    objects.addLast(new ExpressionDotID((Expression) o3, (Token) o2, (ExpressionID) o1)); return true;
////                }
////                break;
////            case "ExpressionID DOT ExpressionFunction":
////            case "ExpressionFunction DOT ExpressionFunction":
////                next = nextTokenSkipSpaces();
////                if (next.getKind() != Kind.LR) {
////                    objects.addLast(new ExpressionDotFunction((Expression) o3, (Token) o2, (ExpressionFunction) o1)); return true;
////                }
////                break;
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

    private boolean priority(Token self, Token other) {
        Integer p1 = PRIORITY.get(self.kind.sign);
        if (null == p1) {
            panic("not a operator sign.", self);
            return false;
        }
        if (null == other.kind.sign) { return true; }
        Integer p2 = PRIORITY.get(other.kind.sign);
        if (null == p2) { return true; }
        return p1 <= p2;
    }

    private boolean digest2() {
        if (channelSizeLess(2)) { return false; }
        Object o1 = removeLast();
        Object o2 = removeLast();
        String status = parseName(o2) + " " + parseName(o1);
        switch (status) {
            case "ExpressionOpMiddle MergeOperatorRight":
//            case "ExpressionID MergeOperatorRight":
                addLast(new ExpressionOpRight((Expression) o2, ((MergeOperatorRight) o1).getToken()));
                tryRemoveSpace();
                return true;
            case "ExpressionID ExpressionRoundBracket":
                addLast(new ExpressionFunction((ExpressionID) o2, (ExpressionRoundBracket) o1));
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
                MergeOperatorLeft left = (MergeOperatorLeft) o2;
                if (priority(left.getToken(), glanceSkipSpace())) {
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
////            case "BIT_NOT ExpressionBool": objects.addLast(new ExpressionLeftOp((Token) o2, (Expression) o1)); return true;
////            case "ExpressionID ADD_ADD":
////            case "ExpressionRightOp ADD_ADD": objects.addLast(new ExpressionRightOp((Expression) o2, (Token) o1)); return true;
////            case "ExpressionID ExpressionRoundBracket": objects.addLast(new ExpressionFunction((Expression) o2, (ExpressionRoundBracket) o1)); return true;
            case "LS RS":
                addLast(new ExpressionSquareBracket((Token) o2, new ExpressionEmpty((Token) o2, (Token) o1),(Token) o1));
                return true;
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
            switch (token.kind) {
                case ID:
                    addLast(new ExpressionID((Token) o1));
                    tryRemoveSpace();
                    return true;
                case LB:
                    if (isChannelEmpty()
                            || (channelSize(1) && getFirst() instanceof ExpressionSquareBracket)
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
                case COLON:
                    // 尝试解析一个对象
                    Expression expression = ExpressionChannel.produce(this, extend(Kind.COLON));
                    if (token.next(expression.first())) {
                        addLast(new ExpressionTrait(token, expression));
                        tryRemoveSpace().acceptColon().refuseAll();
                        return true;
                    } else {
                        panic("should be connected. " + token + " " + expression.first());
                    }
                default:
            }
        }

//        String status = parseName(o1);
//        switch (status) {
//            default:
//        }
        addLast(o1);
        return false;
    }

    @Override
    protected void digested() {
        Token next = glance();
        if (end(next) && next.not(Kind.COLON)) {
            this.full();
        }
    }

    private void full() {
        int size = channelSize();
        if (0 < size) {
            if (getFirst() instanceof Expression) {
                Expression expression = (Expression) removeFirst();
                if (1 == size) {
                    data = expression;
                    return;
                }
                List<ExpressionTrait> restricts = new ArrayList<>(size - 1);
                do {
                    Object n = removeFirst();
                    if (n instanceof ExpressionTrait) {
                        restricts.add((ExpressionTrait) n);
                    } else {
                        panic("expression can only be one expression and restricts");
                    }
                } while (isChannelFull());
                data = new ExpressionWithTrait(expression, restricts);
                return;
            }
        }
        panic("should not end.");
    }

    public static Expression produce(Channel channel, Kind... end) {
        return new ExpressionChannel(channel, end).produce();
    }

}
