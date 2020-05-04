package canoe.ast.stm;

import canoe.lexer.Token;
import canoe.parser.syntax.expression.Expression;
import canoe.parser.syntax.expression.call.ExpressionFunctionCall;
import canoe.parser.syntax.expression.pair.ExpressionRound;
import canoe.parser.syntax.expression.single.ExpressionID;
import canoe.parser.syntax.expression.split.ExpressionDot;
import canoe.util.PanicUtil;

/**
 * @author dawn
 */
public class ExpUtil {

    public static Exp parse(Expression e) {
        String name = e.getClass().getSimpleName();
        switch (name) {
            case "ExpressionDot": return parse((ExpressionDot) e);
            default:
        }
        PanicUtil.panic("what is class name ?!");
        return null;
    }
    private static Exp parse(ExpressionDot e) {
        Expression e1 = e.getLeft();
        Expression e2 = e.getRight();
        String status = e1.getClass().getSimpleName() + " " + e2.getClass().getSimpleName();
        switch (status) {
            case "ExpressionDot ExpressionFunctionCall":
                return new MethodCallExp(parse((ExpressionDot) e1), parse((ExpressionFunctionCall) e2));
            case "ExpressionID ExpressionFunctionCall":
                return new MethodCallExp(new IdentifierExp(new Identifier(((ExpressionID) e1).getID())), parse((ExpressionFunctionCall) e2));

            default:
        }
        PanicUtil.panic("what is status ?! " + status);
        return null;
    }
    private static FunctionCallExp parse(ExpressionFunctionCall e) {
        Token id = e.getId();
        ExpressionRound round = e.getRound();
        return new FunctionCallExp(new Identifier(id), parse(round));
    }
    private static ExpList parse(ExpressionRound round) {
        Expression expression = round.getExpression();
        String name = expression.getClass().getSimpleName();
        switch (name) {
            case "ExpressionDot": return new ExpList(parse((ExpressionDot) expression));
            case "ExpressionEmpty": return new ExpList();

            default:
        }
        PanicUtil.panic("what is class name ?!");
        return null;
    }
}
