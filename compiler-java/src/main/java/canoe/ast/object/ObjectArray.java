package canoe.ast.object;

import canoe.lexis.Token;

import java.util.List;

/**
 * @author dawn
 */
public class ObjectArray extends Object {

    private Token id;

    private Token ls;

    private Token number;

    private Token rs;

    private Token lb;

    private List<Object> objects;

    private Token rb;


    public ObjectArray(Token id, Token ls, Token number, Token rs, Token lb, List<Object> objects, Token rb) {
        this.id = id;
        this.ls = ls;
        this.number = number;
        this.rs = rs;
        this.lb = lb;
        this.objects = objects;
        this.rb = rb;
    }
}
