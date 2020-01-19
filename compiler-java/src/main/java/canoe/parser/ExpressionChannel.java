package canoe.parser;

import canoe.ast.expression.Expression;
import canoe.lexis.Kind;
import canoe.lexis.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static canoe.util.Util.panic;

/**
 * @author dawn
 */
public class ExpressionChannel {

    private String fileName;

    private TokenReader reader;

    private int index;

    private List<Expression> expressions = new ArrayList<>();

    private List<Token> tokens = new ArrayList<>();

    private Stack<Token> stack = new Stack<>();

    ExpressionChannel(String fileName, TokenReader reader) {
        this.fileName = fileName;
        this.reader = reader;
        Token next = reader.nextToken(false);
        switch (next.getKind()) {
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
        this.index = reader.getIndex();
    }

    boolean done() {
        Token next = reader.nextToken(false);

        switch (next.getKind()) {
            // [ 和 {
            case LS: case LB: stack.add(next); break;
            // ] 和 }
            case RS: case RB: if (stack.empty()) { return true; }
                Token top = stack.pop();
                if (top.getKind() == Kind.LS) {
                    if (next.getKind() == Kind.RS) { break; } else { panicToken("try find ] match with: " + top, next); }
                } else if (top.getKind() == Kind.LB) {
                    if (next.getKind() == Kind.RB) { break; } else { panicToken("try find } match with: " + top, next); }
                } else { panicToken("token can not be.", next); }
                return true;

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

        // 移进



        // 归约



        return false;
    }

    Expression getExpression() {
        return null;
    }

    private void panicToken(String tip, Token token) {
        panic(tip, fileName, token);
    }
}
