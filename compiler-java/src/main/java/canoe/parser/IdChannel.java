package canoe.parser;

import canoe.ast.expression.*;
import canoe.ast.statement.Statements;
import canoe.lexis.Kind;
import canoe.lexis.Token;

import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

import static canoe.util.Util.panic;

/**
 * @author dawn
 */
public class IdChannel {

    private String fileName;

    private TokenReader reader;

    private int index;

    private ConcurrentLinkedDeque<Object> objects = new ConcurrentLinkedDeque<>();

    private Stack<Token> stack = new Stack<>();

    IdChannel(String fileName, TokenReader reader, Supplier<Statements> parseStatements) {
        this.fileName = fileName;
        this.reader = reader;
        Token next = reader.nextToken(false);
        switch (next.getKind()) {
            case ID: break;
            default: panicToken("can not be this kind of token.", next);
        }
        this.index = reader.getIndex();
        while (!done()) {}
    }

    boolean done() {
        Token next = reader.nextToken(false);
        Token top;
        switch (next.getKind()) {
            case TRUE:
            case FALSE:
            case NUMBER_HEXADECIMAL:
            case NUMBER_DECIMAL:
            case NUMBER_OCTAL:
            case NUMBER_BINARY:
            case REAL_DECIMAL:
            case STRING:
            case DOT:
            case ID: break;

            case COLON:
            case ADD: case SUB:  case MUL: case DIV: case MOD:
            case ADD_ASSIGN: case SUB_ASSIGN: case MUL_ASSIGN: case DIV_ASSIGN: case MOD_ASSIGN:
            case BIT_MOVE_LEFT_ASSIGN: case BIT_MOVE_RIGHT_ASSIGN:
            case BIT_AND_ASSIGN: case BIT_OR_ASSIGN: case BIT_XOR_ASSIGN:
            case ADD_ADD: case SUB_SUB:
                return true;

            case LR: stack.add(next); break;
            case RR:
                if (stack.empty()) { panicToken("can not be )", next); }
                top = stack.pop();
                if (top.getKind() == Kind.LR) { break; } else { panicToken("try find ) match with: " + top, next); }

            case COMMA:
                break;

            case SPACES: reader.nextToken(); return done();

//            case CR: if (expressions.size() == 1 && tokens.isEmpty() && stack.empty()) { return true; }

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

            // 无法归约的单个Expression
            case "ExpressionID" : break;
            case "ExpressionDotID" : break;
            case "ExpressionDotFunction" : break;
            case "ExpressionComma" : break;
            case "ExpressionFunction" : break;

            // 无法归约的 Expression Token
            case "ExpressionID DOT": break;
            case "ExpressionDotFunction DOT": break;
            case "ExpressionID COMMA": break;
            case "ExpressionComma COMMA": break;
            case "ExpressionID LR": break;
            case "ExpressionFunction DOT": break;

            // 无法归约的 Expression Token Expression
            case "ExpressionID DOT ExpressionID": break;
            case "ExpressionFunction DOT ExpressionID": break;
            case "ExpressionDotFunction DOT ExpressionID": break;
            case "ExpressionID LR ExpressionID": break;
            case "ExpressionID LR ExpressionDotFunction": break;

            // 无法归约的 Expression Token Expression Token
            case "ExpressionID DOT ExpressionID LR": break;
            case "ExpressionDotFunction DOT ExpressionID LR": break;
            case "ExpressionFunction DOT ExpressionID LR": break;
            case "ExpressionID LR ExpressionID DOT": break;

            case "ExpressionID DOT ExpressionID LR ExpressionBool": break;
            case "ExpressionDotFunction DOT ExpressionID LR ExpressionBool": break;
            case "ExpressionID DOT ExpressionID LR ExpressionNumber": break;

            case "ExpressionID DOT ExpressionID LR ExpressionBool COMMA": break;
            case "ExpressionDotFunction DOT ExpressionID LR ExpressionBool COMMA": break;

            case "ExpressionID DOT ExpressionID LR ExpressionComma": break;
            case "ExpressionDotFunction DOT ExpressionID LR ExpressionComma": break;
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
            case "ExpressionDotFunction DOT ExpressionFunction":
            case "ExpressionID DOT ExpressionFunction":
            case "ExpressionFunction DOT ExpressionFunction":
                next = nextTokenSkipSpaces();
                if (next.getKind() != Kind.LR) {
                    objects.addLast(new ExpressionDotFunction((Expression) o3, (Token) o2, (ExpressionFunction) o1)); return true;
                }
                break;
            case "ExpressionDotFunction DOT ExpressionID":
            case "ExpressionID DOT ExpressionID":
                next = nextTokenSkipSpaces();
                if (next.getKind() != Kind.LR) {
                    objects.addLast(new ExpressionDotID((Expression) o3, (Token) o2, (ExpressionID) o1)); return true;
                }
                break;
            case "ExpressionBool COMMA ExpressionNumber":
            case "ExpressionID COMMA ExpressionID":
            case "ExpressionComma COMMA ExpressionID":
                objects.addLast(new ExpressionComma((Expression) o3, (Token) o2, (Expression) o1)); return true;
            case "LR ExpressionNumber RR":
            case "LR ExpressionComma RR":
            case "LR ExpressionDotFunction RR":
                objects.addLast(new ExpressionRoundBracket((Token) o3, (Expression) o2, (Token) o1)); return true;

            default:
        }
        objects.addLast(o3);
        objects.addLast(o2);
        objects.addLast(o1);
        return false;
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
            case "ExpressionID ExpressionRoundBracket":
                objects.addLast(new ExpressionFunction((Expression) o2, (ExpressionRoundBracket) o1)); return true;
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
                    case ADD: case SUB:  case MUL: case DIV: case MOD:

                    case DOT:
                    case COMMA:
                    case ADD_ASSIGN: case SUB_ASSIGN: case MUL_ASSIGN: case DIV_ASSIGN: case MOD_ASSIGN:
                    case BIT_MOVE_LEFT_ASSIGN: case BIT_MOVE_RIGHT_ASSIGN:
                    case BIT_AND_ASSIGN: case BIT_OR_ASSIGN: case BIT_XOR_ASSIGN:
                    case ADD_ADD: case SUB_SUB:
                    case LR:
                    case CR:
                    case COLON:
                        objects.addLast(new ExpressionID((Token) o1));
                        return true;
                    default:
                }
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
        for (Object o : objects) {
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
        if (objects.size() != 1 || !(objects.getLast() instanceof Expression)) { return false; }
        int index = reader.getIndex();
        removeSpace();
        Token next = reader.nextToken();
        boolean result = false;
        switch (next.getKind()) {
            case ADD: case SUB:  case MUL: case DIV: case MOD:
            case ADD_ASSIGN: case SUB_ASSIGN: case MUL_ASSIGN: case DIV_ASSIGN: case MOD_ASSIGN:
            case BIT_MOVE_LEFT_ASSIGN: case BIT_MOVE_RIGHT_ASSIGN:
            case BIT_AND_ASSIGN: case BIT_OR_ASSIGN: case BIT_XOR_ASSIGN:
            case ADD_ADD: case SUB_SUB:
            case COLON:
            case CR:
            case LB:
            case IN: result = true; break;
            default:
        }
        reader.move(index);
        return result;
    }

    Expression getExpression(boolean force) {
        if (!isDone()) {
            if (force) {
                panicToken("expression is not done.", reader.thisToken());
            } else {
                reader.move(index);
                return null;
            }
        }
        return (Expression) objects.getLast();
    }

    void recover() {
        reader.move(this.index);
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
