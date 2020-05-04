package canoe.ast.stm;

import canoe.parser.syntax.statement.Statement;
import canoe.parser.syntax.statement.StatementExpression;
import canoe.util.PanicUtil;

/**
 * @author dawn
 */
public class StmUtil {

    public static Stm parse(Statement s) {
        String name = s.getClass().getSimpleName();
        switch (name) {
            case "StatementExpression": return parse((StatementExpression) s);
            default:
        }
        PanicUtil.panic("what is class name ?!");
        return null;
    }

    private static Stm parse(StatementExpression s) {
        return new ExpStm(ExpUtil.parse(s.getExpression()));
    }

}
