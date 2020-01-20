package canoe.ast.statement.desc;

import canoe.lexis.Token;

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
