package canoe.ast.stm;

/**
 * @author dawn
 */
public class IdentifierExp implements Exp {

    private Identifier id;

    public IdentifierExp(Identifier id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
