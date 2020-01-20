package canoe.ast.statement.desc;

import canoe.lexis.Token;

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
