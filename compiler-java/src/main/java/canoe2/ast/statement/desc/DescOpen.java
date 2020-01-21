package canoe2.ast.statement.desc;

import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class DescOpen implements Desc {

    private Token colon;

    private Token open;

    public DescOpen(Token colon, Token open) {
        this.colon = colon;
        this.open = open;
    }
}
