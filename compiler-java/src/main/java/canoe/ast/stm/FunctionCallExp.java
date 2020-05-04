package canoe.ast.stm;

/**
 * @author dawn
 */
public class FunctionCallExp implements Exp {

    private Identifier id;
    private ExpList params;

    public FunctionCallExp(Identifier id, ExpList params) {
        this.id = id;
        this.params = params;
    }

    @Override
    public String toString() {
        return id.toString() + params.toString() ;
    }
}
