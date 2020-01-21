package canoe2.ast.statement.desc;

import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class DescGoto implements Desc {

    private Token colon;

    private Token gotoToken;

    public DescGoto(Token colon, Token gotoToken) {
        this.colon = colon;
        this.gotoToken = gotoToken;
    }
}
