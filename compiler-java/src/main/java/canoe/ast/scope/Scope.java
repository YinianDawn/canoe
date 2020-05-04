package canoe.ast.scope;

import java.util.LinkedHashMap;

/**
 * @author dawn
 */
public class Scope {

    private Scope base;
    private int layer;
    private LinkedHashMap<String, AstScope> symbols = new LinkedHashMap<>();
    private LinkedHashMap<String, AstScope> overloads = new LinkedHashMap<>();

    public Scope(Scope base, int layer) {
        this.base = base;
        this.layer = layer;
    }

}
