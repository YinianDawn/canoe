package canoe.ast.stm;

/**
 * @author dawn
 */
public class ExpStm implements Stm {

    private Exp exp;

    public ExpStm(Exp exp) {
        this.exp = exp;
    }

    @Override
    public String toString() {
        return exp.toString();
    }
}
