package canoe.parser.channel.expression;

import canoe.ast.expression.*;
import canoe.ast.merge.MergeOperatorBoth;
import canoe.ast.merge.MergeOperatorRight;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.channel.statement.special.IfChannel;
import canoe.parser.channel.statement.special.MatchChannel;

import java.util.HashMap;

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
        PRIORITY.put("->", 1);
        PRIORITY.put(".", 2);
        PRIORITY.put("..", 2);
        // ! + - ++ -- 单目运算符 从右到左
        PRIORITY.put("!", 2);
        PRIORITY.put("+l", 2);
        PRIORITY.put("-l", 2);
        PRIORITY.put("++", 2);
        PRIORITY.put("--", 2);
        // * / % 双目运算符 从左到右
        PRIORITY.put("*", 3);
        PRIORITY.put("/", 3);
        PRIORITY.put("%", 3);
        // + - 双目运算符 从左到右
        PRIORITY.put("+", 4);
        PRIORITY.put("-", 4);
        // >> << 位移运算符 双目 从左到右
        PRIORITY.put(">>", 5);
        PRIORITY.put("<<", 5);
        // < <= > >= 关系运算符 双目 从左到右
        PRIORITY.put(">", 6);
        PRIORITY.put(">=", 6);
        PRIORITY.put("<", 6);
        PRIORITY.put("<=", 6);
        // == != 关系运算符 双目 从左到右
        PRIORITY.put("==", 7);
        PRIORITY.put("!=", 7);
        // & 按位与 双目 从左到右
        PRIORITY.put("&", 8);
        // & 按位异或 双目 从左到右
        PRIORITY.put("^", 9);
        // & 按位或 双目 从左到右
        PRIORITY.put("|", 10);
        // & 逻辑与 双目 从左到右
        PRIORITY.put("&&", 11);
        // & 逻辑或 双目 从左到右
        PRIORITY.put("||", 12);
        // := = += -= /= %= >>= <<= &= |= 赋值运算 双目 从右到左
        PRIORITY.put(":=", 13);
        PRIORITY.put("=", 13);
        PRIORITY.put("+=", 13);
        PRIORITY.put("-=", 13);
        PRIORITY.put("*=", 13);
        PRIORITY.put("/=", 13);
        PRIORITY.put("%=", 13);
        PRIORITY.put(">>=", 13);
        PRIORITY.put("<<=", 13);
        PRIORITY.put("&=", 13);
        PRIORITY.put("^=", 13);
        PRIORITY.put("|=", 13);
        // , 运算 双目 从左到右
        PRIORITY.put(",", 14);
    }

//    private Stack<Token> pairSign = new Stack<>();

    private ExpressionChannel(Channel channel, Kind... end) {
        super(channel, end);
        Token next = glance();
        if (!COMMON_KEY_WORDS.contains(next.kind)
                && !LEFT_OPERATOR.contains(next.kind)
                && !CONSTANT.contains(next.kind)) {
            if (end(next)) { panic("can not start with " + next.kind.getSign(), next); }
            switch (next.kind) {
                case LR:
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
                break;
            // 单个运算符

            // 表达式 运算符
            case "ExpressionID MergeOperatorBoth":
            case "ExpressionOpMiddle MergeOperatorBoth":
            case "ExpressionOpMiddle LR":
            case "ExpressionOpMiddle ExpressionRoundBracket":
                break;

            // 表达式 运算符 表达式
            case "ExpressionOpMiddle LR ExpressionConstant":
            case "ExpressionOpMiddle LR ExpressionOpMiddle":
            case "ExpressionID MergeOperatorBoth ExpressionID":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID":
                break;

            // 表达式 运算符 表达式 运算符
            case "ExpressionID MergeOperatorBoth ExpressionID LR":
            case "ExpressionOpMiddle MergeOperatorBoth ExpressionID LR":
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
                // 检查运算符优先级
                op = ((MergeOperatorBoth) o2).getToken();
                if (priority(op, glanceSkipSpace())) {
                    addLast(new ExpressionOpMiddle((Expression) o3, op, (Expression) o1));
                    return true;
                }
                break;
            case "LR ExpressionOpMiddle RR":
            case "LR ExpressionConstant RR":
                addLast(new ExpressionRoundBracket((Token) o3, (Expression) o2, (Token) o1));
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
////            case "LR ExpressionDotFunction RR":
////                objects.addLast(new ExpressionRoundBracket((Token) o3, (Expression) o2, (Token) o1)); return true;
            default:
        }
        addLast(o3);
        addLast(o2);
        addLast(o1);
        return false;
    }

    private boolean priority(Token self, Token other) {
        Integer p1 = PRIORITY.get(self.kind.getSign());
        if (null == p1) {
            panic("not a operator sign.", self);
        }
        if (null == other.kind.getSign()) { return true; }
        Integer p2 = PRIORITY.get(other.kind.getSign());
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
                ignoreSpace();
                return true;
            case "ExpressionID ExpressionRoundBracket":
                addLast(new ExpressionFunction((ExpressionID) o2, (ExpressionRoundBracket) o1));
                ignoreSpace();
                return true;
////            case "BIT_NOT ExpressionBool": objects.addLast(new ExpressionLeftOp((Token) o2, (Expression) o1)); return true;
////            case "ExpressionID ADD_ADD":
////            case "ExpressionRightOp ADD_ADD": objects.addLast(new ExpressionRightOp((Expression) o2, (Token) o1)); return true;
////            case "ExpressionID ExpressionRoundBracket": objects.addLast(new ExpressionFunction((Expression) o2, (ExpressionRoundBracket) o1)); return true;
////            case "LR RR":
////                objects.addLast(new ExpressionRoundBracket((Token) o2, new ExpressionEmpty(),(Token) o1)); return true;

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
            if (token.is(Kind.ID)) {
                addLast(new ExpressionID((Token) o1));
                ignoreSpace();
                return true;
            }
            if (CONSTANT.contains(token.kind)) {
                addLast(new ExpressionConstant(token));
                ignoreSpace();
                return true;
            }
            if (BINARY_OPERATOR.contains(token.kind)) {
                addLast(new MergeOperatorBoth(token));
                ignoreSpace();
                return true;
            }
            if (RIGHT_OPERATOR.contains(token.kind)) {
                addLast(new MergeOperatorRight(token));
                ignoreSpace();
                return true;
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
        if (end(glanceSkipSpace())) {
            this.full();
        }
    }

    private void full() {
        if (channelSize(1) && getLast() instanceof Expression) {
            data = (Expression) removeLast();
            return ;
        }
        panic("should not end.");
    }

    public static Expression produce(Channel channel, Kind... end) {
        return new ExpressionChannel(channel, end).produce();
    }

}
