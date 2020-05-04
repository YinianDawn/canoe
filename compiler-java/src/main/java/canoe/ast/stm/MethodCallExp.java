package canoe.ast.stm;

/**
 * @author dawn
 */
public class MethodCallExp implements Exp {

    private Exp exp;
    private FunctionCallExp callExp;

    public MethodCallExp(Exp exp, FunctionCallExp callExp) {
        this.exp = exp;
        this.callExp = callExp;
    }

    @Override
    public String toString() {
        return exp.toString() + "." + callExp.toString();
    }
}
