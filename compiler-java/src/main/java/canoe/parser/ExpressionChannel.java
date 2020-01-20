package canoe.parser;

import canoe.ast.expression.*;
import canoe.ast.statement.StatementIf;
import canoe.ast.statement.StatementMatch;
import canoe.ast.statement.Statements;
import canoe.lexis.Kind;
import canoe.lexis.Token;

import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

import static canoe.util.Util.panic;

/**
 * @author dawn
 */
public class ExpressionChannel {

    private static HashMap<String, Integer> PRIORITY = new HashMap<>(97);

    static {
        // ( ) [ ] -> 后缀运算符 从左到右
        PRIORITY.put("(", 1);
        PRIORITY.put(")", 1);
        PRIORITY.put("[", 1);
        PRIORITY.put("]", 1);
        PRIORITY.put("->", 1);
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

    private String fileName;

    private TokenReader reader;

    private Supplier<Statements> parseStatements;
    private Supplier<StatementIf> parseStatementIf;
    private Supplier<StatementMatch> parseStatementMatch;

    private ConcurrentLinkedDeque<Object> objects = new ConcurrentLinkedDeque<>();

    private Stack<Token> stack = new Stack<>();

    ExpressionChannel(String fileName, TokenReader reader,
                      Supplier<Statements> parseStatements,
                      Supplier<StatementIf> parseStatementIf,
                      Supplier<StatementMatch> parseStatementMatch) {
        this.fileName = fileName;
        this.reader = reader;
        Token next = reader.nextToken(false);
        switch (next.getKind()) {
            case CANOE:
            case RR:
            case IF:
            case MATCH:
            case TRUE:
            case FALSE:
            case NUMBER_HEXADECIMAL:
            case NUMBER_DECIMAL:
            case NUMBER_OCTAL:
            case NUMBER_BINARY:
            case REAL_DECIMAL:
            case STRING:
            case LR:
            case BIT_NOT:
            case ID: break;
            default: panicToken("can not be this kind of token.", next);
        }
        this.parseStatements = parseStatements;
        this.parseStatementIf = parseStatementIf;
        this.parseStatementMatch = parseStatementMatch;
        while (!done()) {}
    }

    boolean done() {
        Token next = reader.nextToken(false);

        // 需要判断终止条件

        // 遇到左花括号 { 谨慎通过
        if (next.getKind() == Kind.LB) {
            if (!objects.isEmpty()) {
                java.lang.Object o = objects.getLast();
                if (o instanceof Token) {
                    Token last = (Token) o;
                    switch (last.getKind()) {
                        case RS:
                        case LAMBDA: break;
                        // 不允许继续找表达式了
                        default: return true;
                    }

                } else {
                    // 上一个不是 token 就不允许继续找表达式了
                    return true;
                }
            }
        }


        Token top;
        switch (next.getKind()) {
            // [ 和 {
            case LS: case LB: stack.add(next); break;
            // ] 和 }
            case RS: case RB: if (stack.empty()) { return true; }
                top = stack.pop();
                if (top.getKind() == Kind.LS) {
                    if (next.getKind() == Kind.RS) { break; } else { panicToken("try find ] match with: " + top, next); }
                } else if (top.getKind() == Kind.LB) {
                    if (next.getKind() == Kind.RB) { break; } else { panicToken("try find } match with: " + top, next); }
                } else { panicToken("token can not be.", next); }
                return true;

            case IF:
                StatementIf statementIf = parseStatementIf.get();
                objects.addLast(new ExpressionIf(statementIf));
                return done();
            case MATCH:
                StatementMatch statementMatch = parseStatementMatch.get();
                objects.addLast(new ExpressionMatch(statementMatch));
                return done();

            case CANOE:
            case DOT:

            case TRUE:
            case FALSE:
            case NUMBER_HEXADECIMAL:
            case NUMBER_DECIMAL:
            case NUMBER_OCTAL:
            case NUMBER_BINARY:
            case REAL_DECIMAL:
            case STRING:
            case BIT_NOT:
            case ID: break;

            case LR: stack.add(next); break;
            case RR:
                if (!stack.empty()) {
                    top = stack.pop();
                    if (top.getKind() == Kind.LR) { break; } else { panicToken("try find ) match with: " + top, next); }
                } else {
                    return true;
                }

            case EQ: case NE: case GT: case GE: case LT: case LE:
            case ADD: case SUB:  case MUL: case DIV: case MOD:
            case ADD_ADD: case SUB_SUB:
            case LAMBDA:
            case COMMA:
                break;

            case SPACES: reader.nextToken(); return done();

            case IN: return true;

            case CR: if (objects.size() == 1 && objects.getLast() instanceof Expression) { return true; }
            case COLON: if (objects.size() == 1 && objects.getLast() instanceof Expression) { return true; }

            default: panicToken("can not be this kind of token.", next);
        }

        // 移进
        objects.addLast(reader.nextToken());

        // 归约
        reduce();

        return isDone();
    }


    private void reduce() {
        boolean constant = false;
        while (reduceConstant()) { constant = true; }
        if (constant) { reduce(); return; }

        if (reduce1()) { reduce(); return; }

        if (reduce2()) { reduce(); return; }

        if (reduce3()) { reduce(); return; }

        String status = getStatus();
        switch (status) {
            // 无法归约的单个Token
            case "LR": break;
            case "BIT_NOT": break;

            // 无法归约的单个Expression
            case "ExpressionBool" : break;
            case "ExpressionNumber" : break;
            case "ExpressionString" : break;
            case "ExpressionRoundBracket" : break;
            case "ExpressionLeftOp" : break;
            case "ExpressionRightOp" : break;
            case "ExpressionMiddleOp" : break;
            case "ExpressionID" : break;
            case "ExpressionLambdaExpression" : break;
            case "ExpressionLambdaStatements" : break;
            case "ExpressionComma" : break;
            case "ExpressionFunction" : break;
            case "ExpressionDotID" : break;
            case "ExpressionDotFunction" : break;

            // 无法归约的 Token Expression
            case "LR ExpressionNumber": break;

            // 无法归约的 Expression Token
            case "ExpressionNumber ADD": break;
            case "ExpressionID LAMBDA": break;
            case "ExpressionComma LAMBDA": break;
            case "ExpressionID COMMA": break;
            case "ExpressionID ADD": break;
            case "ExpressionID LR": break;
            case "ExpressionID GT": break;
            case "ExpressionID DOT": break;
            case "ExpressionDotID DOT": break;
            case "ExpressionFunction DOT": break;

            // 无法归约的 Expression Token Expression
            case "ExpressionID LAMBDA ExpressionID": break;
            case "ExpressionID LR ExpressionNumber": break;
            case "ExpressionID LR ExpressionID": break;
            case "ExpressionID LR ExpressionDotFunction": break;
            case "ExpressionFunction DOT ExpressionID": break;

            // 无法归约的 Expression Token Expression Token
            case "ExpressionID LAMBDA ExpressionID ADD": break;
            case "ExpressionID LR ExpressionID DOT": break;
            case "ExpressionFunction DOT ExpressionID LR": break;

            case "ExpressionID LR ExpressionID DOT ExpressionID": break;
            case "ExpressionID LR ExpressionID DOT ExpressionID LR": break;

            default: panicToken("what is status.", reader.thisToken());
        }
    }

    private boolean reduce3() {
        if (objects.size() <= 2) { return false; }
        Object o1 = objects.removeLast();
        Object o2 = objects.removeLast();
        Object o3 = objects.removeLast();
        String status = getKind(o3) + " " + getKind(o2) + " " + getKind(o1);
        Token next;
        switch (status) {
            case "LR ExpressionNumber RR": objects.addLast(new ExpressionRoundBracket((Token) o3, (Expression) o2, (Token) o1)); return true;
            case "ExpressionID ADD ExpressionID":
            case "ExpressionID ADD ExpressionNumber":
            case "ExpressionNumber ADD ExpressionNumber":
            case "ExpressionID GT ExpressionNumber":
                // 检查运算符优先级
                Token op = (Token) o2;
                if (priority3(op)) {
                    objects.addLast(new ExpressionMiddleOp((Expression) o3, op, (Expression) o1));
                    return true;
                }
                break;
            case "ExpressionID LAMBDA ExpressionMiddleOp":
            case "ExpressionID LAMBDA ExpressionID":
                next = nextTokenSkipSpaces();
                if (next.getKind() == Kind.CR) {
                    objects.addLast(new ExpressionLambdaExpression((Expression) o3, (Token) o2, (Expression) o1));
                    return true;
                }
                break;
            case "ExpressionID COMMA ExpressionID":
                next = nextTokenSkipSpaces();
                switch (next.getKind()) {
                    case LAMBDA:
                    case IN:
                        objects.addLast(new ExpressionComma((Expression) o3, (Token) o2, (Expression) o1));
                        return true;

                    default:
                }
                break;
            case "ExpressionComma LAMBDA LB":
                Statements statements = parseStatements.get();
                removeSpaceOrCR();
                next = reader.nextToken();
                if (next.getKind() != Kind.RB) {
                    panicToken("can not be.", next);
                }
                objects.addLast(new ExpressionLambdaStatements((Expression) o3, (Token) o2, (Token) o1, statements, next));
                return true;
            case "ExpressionDotID DOT ExpressionID":
            case "ExpressionID DOT ExpressionID":
                next = nextTokenSkipSpaces();
                if (next.getKind() != Kind.LR) {
                    objects.addLast(new ExpressionDotID((Expression) o3, (Token) o2, (ExpressionID) o1)); return true;
                }
                break;
            case "ExpressionID DOT ExpressionFunction":
            case "ExpressionFunction DOT ExpressionFunction":
                next = nextTokenSkipSpaces();
                if (next.getKind() != Kind.LR) {
                    objects.addLast(new ExpressionDotFunction((Expression) o3, (Token) o2, (ExpressionFunction) o1)); return true;
                }
                break;
            case "LR ExpressionDotFunction RR":
                objects.addLast(new ExpressionRoundBracket((Token) o3, (Expression) o2, (Token) o1)); return true;

            default:
        }
        objects.addLast(o3);
        objects.addLast(o2);
        objects.addLast(o1);
        return false;
    }

    private boolean priority3(Token first) {
        Integer p1 = PRIORITY.get(first.getKind().getKey());
        if (null == p1) {
            panicToken("wrong token", first);
        }
        Token next = nextTokenSkipSpaces();
        Integer p2 = PRIORITY.get(next.getKind().getKey());
        if (null == p2) { return true; }
        return p2 <= p1;
    }

    private Token nextTokenSkipSpaces() {
        int index = reader.getIndex();
        removeSpace();
        Token token = reader.nextToken();
        reader.move(index);
        return token;
    }

    private boolean reduce2() {
        if (objects.size() <= 1) { return false; }
        Object o1 = objects.removeLast();
        Object o2 = objects.removeLast();
        String status = getKind(o2) + " " + getKind(o1);
        switch (status) {
            case "BIT_NOT ExpressionBool": objects.addLast(new ExpressionLeftOp((Token) o2, (Expression) o1)); return true;
            case "ExpressionID ADD_ADD":
            case "ExpressionRightOp ADD_ADD": objects.addLast(new ExpressionRightOp((Expression) o2, (Token) o1)); return true;
            case "ExpressionID ExpressionRoundBracket": objects.addLast(new ExpressionFunction((Expression) o2, (ExpressionRoundBracket) o1)); return true;
            case "LR RR":
                objects.addLast(new ExpressionRoundBracket((Token) o2, new ExpressionEmpty(),(Token) o1)); return true;

            default:
        }
        objects.addLast(o2);
        objects.addLast(o1);
        return false;
    }

    private boolean reduce1() {
        if (objects.isEmpty()) { return false; }
        Object o1 = objects.removeLast();
        String status = getKind(o1);
        switch (status) {
            case "ID":
                Token next = nextTokenSkipSpaces();
                switch (next.getKind()) {
                    case EQ: case NE: case GT: case GE: case LT: case LE:
                    case ADD_ADD: case SUB_SUB:
                    case LAMBDA:
                    case ADD: case SUB: case MUL: case DIV: case MOD:
                    case COMMA:
                    case COLON:
                    case CR:
                    case IN:
                    case LR:
                    case LB:
                        objects.addLast(new ExpressionID((Token) o1));
                        return true;
                    default:
                }
            case "CANOE":
                objects.addLast(new ExpressionID((Token) o1));
                return true;
            default:
        }
        objects.addLast(o1);
        return false;
    }

    private boolean reduceConstant() {
        if (objects.isEmpty()) { return false; }
        Object o = objects.removeLast();
        if (o instanceof Token) {
            Token token = (Token) o;
            switch (token.getKind()) {
                case TRUE : objects.add(new ExpressionBool(token)); return true;
                case FALSE: objects.add(new ExpressionBool(token)); return true;
                case NUMBER_DECIMAL: objects.add(new ExpressionNumber(token)); return true;
                case STRING: objects.add(new ExpressionString(token)); return true;

                default:
            }
        }
        objects.addLast(o);
        return false;
    }

    private String getKind(Object o) {
        if (o instanceof Expression) {
            return o.getClass().getSimpleName();
        } else if (o instanceof Token) {
            return ((Token) o).getKind().name();
        }
        panicToken("can not be: " + o, reader.thisToken());
        return "";
    }

    private String getStatus() {
        StringBuilder sb = new StringBuilder();
        for (java.lang.Object o : objects) {
            sb.append(getKind(o)).append(" ");
        }
        if (0 < sb.length()) {
            if (sb.charAt(sb.length() - 1) == ' ') {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return sb.toString();
    }

    private boolean isDone() {
        if (1 <= objects.size()) {
            if (1 < objects.size() || !(objects.getLast() instanceof Expression)) { return false; }
        }
        int index = reader.getIndex();
        removeSpace();
        Token next = reader.nextToken();
        boolean result = false;
        switch (next.getKind()) {
            case COLON:
            case CR:
            case LB:
            case RR:
            case IN: result = true; break;
            default:
        }
        reader.move(index);
        return result;
    }

    Expression getExpression() {
        if (!isDone()) {
            panicToken("expression is not done.", reader.thisToken());
        }
        return 0 < objects.size() ? (Expression) objects.getLast() : new ExpressionEmpty();
    }

    private void removeSpace() {
        while (reader.hasNext() && reader.nextToken(false).isSpaces()) {
            reader.nextToken();
        }
    }

    private void removeSpaceOrCR() {
        while (reader.hasNext() && (reader.nextToken(false).isSpaces() || reader.nextToken(false).isCR())) {
            reader.nextToken();
        }
    }

    private void panicToken(String tip, Token token) {
        panic(tip, fileName, token);
    }
}
