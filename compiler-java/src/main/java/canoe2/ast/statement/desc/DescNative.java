package canoe2.ast.statement.desc;

import canoe2.lexis.Token;

/**
 * @author dawn
 */
public class DescNative implements Desc {

    private Token colon;

    private Token nativeToken;

    public DescNative(Token colon, Token nativeToken) {
        this.colon = colon;
        this.nativeToken = nativeToken;
    }
}
