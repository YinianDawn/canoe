package canoe.ast.stm;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public class Identifier {

    private String id;
    private Token token;

    public Identifier(Token token) {
        this.token = token;
        this.id = token.value();
    }

    @Override
    public String toString() {
        return id;
    }
}
