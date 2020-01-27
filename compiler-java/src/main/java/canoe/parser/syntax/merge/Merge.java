package canoe.ast.merge;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class Merge {

    protected Token token;

    public Merge(Token token) {
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
