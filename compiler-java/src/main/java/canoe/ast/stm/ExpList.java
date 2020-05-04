package canoe.ast.stm;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dawn
 */
public class ExpList {
    private List<Exp> exps;

    public ExpList(List<Exp> exps) {
        this.exps = exps;
    }

    public ExpList() {
        exps = Collections.emptyList();
    }

    public ExpList(Exp exp) {
        exps = Collections.singletonList(exp);
    }

    @Override
    public String toString() {
        return "(" + exps.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
    }
}
