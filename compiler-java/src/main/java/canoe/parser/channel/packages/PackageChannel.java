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
import static canoe.lexer.KindSet.contains;

/**
 * @author dawn
 */
public class PackageChannel extends Channel<PackageInfo> {

    private PackageChannel(String name, TokenStream stream, Kind... end) {
        super(name, stream, end);
        init();
    }

    @Override
    protected void init() {
        removeSpaceOrCR();
        if (glance().is(Kind.PACKAGE)) {
            while (hunger()) { eat(); }
        } else {
            data = new PackageInfo(new Token(Kind.PACKAGE, null, 0, 0, 7), Collections.emptyList());
        }
    }

    @Override
    protected boolean hunger() {
        return null == data || !end();
    }

    @Override
    protected void eat() {
        Token next = glance();
        if (!pass(next)) {
            // 所有都通过
//        switch (next.kind) {
//            default:
//        }
        }
        if (over(next)) { return; }

        addLast(next());
        clear();

        digest();
    }

    @Override
    protected void digest() {
        String status = status();

        Token last = getLastToken();
        if (null != last) {
            if (1 < channelSize() && contains(COMMON_KEY_WORDS, last)) {
                accept(DOT).refuseAll(); return;
            }
            if (last.is(ID)) {
                accept(DOT).acceptSpaces().refuseAll().over(this::full); return;
            }
            if (last.is(DOT)) {
                accept(ID).acceptKeyWords().refuseAll(); return;
            }
            if (3 <= channelSize() && last.is(SPACES)) {
                if (glance().isCR()) {
                    removeLast(); refuseAll().over(this::full); return;
                } else {
                    panic(last);
                }
            }
        }

        switch (status) {
            case "PACKAGE":
                accept(Kind.SPACES).refuseAll(); break;
            case "PACKAGE SPACES":
                removeLast(); accept(ID).acceptKeyWords().refuseAll(); break;
            default: panic("wrong package statement.");
        }
    }

    @Override
    protected void full() {
        Token packageToken = (Token) removeFirst();
        List<Token> names = new ArrayList<>(channelSize());
        while (channelFull()) {
            names.add((Token) removeFirst());
        }
        data = new PackageInfo(packageToken, names);
    }

    public static PackageInfo produce(String name, TokenStream stream, Kind... end) {
        return new PackageChannel(name, stream, end).produce();
    }

}
