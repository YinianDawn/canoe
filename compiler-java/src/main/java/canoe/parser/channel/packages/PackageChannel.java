package canoe.parser.channel.packages;

import canoe.ast.PackageInfo;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.TokenStream;
import canoe.parser.channel.Channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static canoe.lexer.Kind.*;
import static canoe.lexer.KindSet.COMMON_KEY_WORDS;

/**
 * @author dawn
 */
public class PackageChannel extends Channel<PackageInfo> {

    private PackageChannel(String name, TokenStream stream) {
        super(name, stream, CR);
        removeSpaceOrCR();
        if (glance().not(Kind.PACKAGE)) {
            data = new PackageInfo(new Token(Kind.PACKAGE, null, 0, 0, 7), Collections.emptyList());
        }
        init();
    }

    @Override
    protected void digest() {
        int size = channelSize();
        if (1 < size) {
            Token last = getLastToken();
            if (null != last) {
                if (last.isSpaces()) {
                    if (2 == size) { removeLast();
                        accept(ID).acceptKeyWords().refuseAll(); return;
                    } else {
                        if (glance().isCR()) { removeLast();
                            refuseAll().over(this::full); return;
                        } else {
                            panic(last);
                        }
                    }
                }
                if (contains(last, COMMON_KEY_WORDS)) { accept(DOT).refuseAll(); return; }
                if (last.is(ID)) { accept(DOT).acceptSpaces().refuseAll().over(this::full); return; }
                if (last.is(DOT)) { accept(ID).acceptKeyWords().refuseAll(); return; }
            }
        } else {
            acceptSpaces().refuseAll(); return;
        }
        panic("wrong package statement.");
    }

    private void full() {
        Token packageToken = (Token) removeFirst();
        List<Token> names = new ArrayList<>(channelSize());
        while (channelFull()) { names.add((Token) removeFirst()); }
        data = new PackageInfo(packageToken, names);
    }

    public static PackageInfo produce(String name, TokenStream stream) {
        return new PackageChannel(name, stream).produce();
    }

}
