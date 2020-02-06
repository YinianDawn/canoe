package canoe.parser.channel.packages;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.TokenStream;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.PackageInfo;

/**
 * @author dawn
 */
public class PackageChannel extends Channel<PackageInfo> {

    private PackageChannel(TokenStream stream) {
        super(stream, Kind.CR);
        dropSpacesSemiCR();
        init();
    }

    @Override
    protected void init() {
        if (glance().not(Kind.PACKAGE)) {
            data = new PackageInfo(new Token(Kind.PACKAGE, 1, 1, 7, null),
                    new Token(Kind.UL, 1, 1, 1, "_"));
        }
        super.init();
    }

    @Override
    protected void digest() {
        String status = status();
        switch (status) {
            case "PACKAGE" : acceptSpaces().refuseAll(); break;
            case "PACKAGE SPACES" :
                if (1 < getLastToken().size) {
                    panic("too many blank spaces", getLastToken());
                }
                accept(Kind.ID).refuseAll(); break;
            case "PACKAGE SPACES ID":
                Token symbol = (Token) removeFirst();
                Token name = getLastToken();
                data = new PackageInfo(symbol, name);
                break;
            default: panic("wrong " + Kind.PACKAGE.value + " statement.");
        }
    }

    public static PackageInfo make(TokenStream stream) {
        return new PackageChannel(stream).make();
    }

}
