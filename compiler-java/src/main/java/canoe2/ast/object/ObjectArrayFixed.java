package canoe2.ast.object;

import canoe2.lexis.Token;

import java.util.List;

/**
 * @author dawn
 */
public class ObjectArrayFixed implements Object {

    private Token id;

    private Token ls;

    private Token number;

    private Token rs;

    private Token lb;

    private List<Object> objects;

    private List<Token> commas;

    private Token rb;


    public ObjectArrayFixed(Token id, Token ls, Token number, Token rs, Token lb, List<Object> objects, List<Token> commas, Token rb) {
        this.id = id;
        this.ls = ls;
        this.number = number;
        this.rs = rs;
        this.lb = lb;
        this.objects = objects;
        this.commas = commas;
        this.rb = rb;
    }

    public ObjectArrayFixed(List<Object> objects, List<Token> commas) {
        this.objects = objects;
        this.commas = commas;
    }
}
