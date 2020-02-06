package canoe.parser.channel.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.imports.ImportId;
import canoe.parser.syntax.imports.ImportInclude;

/**
 * @author dawn
 */
public class ImportIncludeChannel extends ImportUtilChannel<ImportInclude> {

    private ImportIncludeChannel(Channel channel, Kind... end) {
        super(channel, end);
        if (glance().not(Kind.INCLUDE)) {
            panic("must be " + Kind.INCLUDE.value, glance());
        }
        init();
    }

    @Override
    protected boolean eat(Token next) {
        if (next.kind == Kind.ID) {
            mark();
        }
        return super.eat(next);
    }

    @Override
    protected void digest() {
        String status = status();

        switch (status) {
            case "INCLUDE": acceptSpaces().accept(Kind.LR).refuseAll(); break;
            case "INCLUDE SPACES": removeLast();
                accept(Kind.ID).accept(Kind.LR).refuseAll(); break;
            case "INCLUDE ID": removeLast(); recover();
                addLast(ImportIdChannel.make(this, Kind.CR));
                over(this::full).refuseAll(); break;
            case "INCLUDE LR": acceptSpaces().accept(Kind.CR).accept(Kind.ID).refuseAll(); break;
            case "INCLUDE LR SPACES": removeLast(); accept(Kind.CR).accept(Kind.ID).refuseAll(); break;
            case "INCLUDE LR CR": removeLast(); acceptSpaces().accept(Kind.CR).accept(Kind.ID).refuseAll(); break;
            case "INCLUDE LR ID": removeLast(); recover();
                LR = (Token) removeLast();
                addLast(ImportIdChannel.make(this, Kind.COMMA, Kind.RR));
                addOthers();
                dropSpacesCR();
                if (glance().not(Kind.RR)) {
                    panic("must be )", glance());
                }
                RR = next();
                over(this::full).refuseAll(); break;
            default: panic("wrong " + Kind.INCLUDE.value + " statement.");
        }
    }

    private void full() {
        Token symbol = (Token) removeFirst();
        ImportId first = (ImportId) removeFirst();
        if (channelFull()) {
            panic("wrong " + Kind.INCLUDE.value + " statement", symbol);
        }
        data = new ImportInclude(symbol, LR, first, others, RR);
    }

    static ImportInclude make(Channel channel, Kind... end) {
        return new ImportIncludeChannel(channel, end).make();
    }

}
