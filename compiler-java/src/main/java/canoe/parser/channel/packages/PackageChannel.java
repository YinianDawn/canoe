package canoe.parser.channel.packages;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.TokenStream;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.PackageInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static canoe.lexer.KindSet.SINGLE_KEY_WORDS;

/**
 * @author dawn
 */
public class PackageChannel extends Channel<PackageInfo> {

    private PackageChannel(TokenStream stream) {
        super(stream, Kind.CR);
        dropSpacesOrCR();
        if (glance().not(Kind.PACKAGE)) {
            data = new PackageInfo(new Token(Kind.PACKAGE, 0, 0, 7, null), Collections.emptyList());
        }
        init();
    }

    @Override
    protected void digest() {
        int size = channelSize();
        if (1 < size) {
            Token last = getLastToken(false);
            if (null != last) {
                if (last.isSpaces()) {
                    if (2 == size) { removeLast();
                        accept(Kind.ID).acceptKeyWords().refuseAll(); return;
                    } else {
                        if (glance().isCR()) { removeLast();
                            refuseAll().over(this::full); return;
                        } else {
                            panic(last);
                        }
                    }
                }
                if (last.is(Kind.DOT)) { accept(Kind.ID).acceptKeyWords().refuseAll(); return; }
                if (contains(last, SINGLE_KEY_WORDS)) { accept(Kind.DOT).refuseAll(); return; }
                if (last.is(Kind.ID)) { accept(Kind.DOT).acceptSpaces().refuseAll().over(this::full); return; }
            }
        } else {
            acceptSpaces().refuseAll(); return;
        }
        panic("wrong " + Kind.PACKAGE.value + " statement.");
    }

    private void full() {
        Token symbol = (Token) removeFirst();
        List<Token> info = new ArrayList<>(channelSize());
        while (channelFull()) { info.add((Token) removeFirst()); }
        data = new PackageInfo(symbol, info);
    }

    public static PackageInfo make(TokenStream stream) {
        return new PackageChannel(stream).make().make(stream.name());
    }

}
