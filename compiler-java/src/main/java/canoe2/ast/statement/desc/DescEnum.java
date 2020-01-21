package canoe2.ast.statement.desc;

import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class DescEnum implements Desc {

    private Token colon;

    private Token enumToken;

    public DescEnum(Token colon, Token enumToken) {
        this.colon = colon;
        this.enumToken = enumToken;
    }
}
