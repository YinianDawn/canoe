package canoe.parser.channel.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.imports.ImportAs;
import canoe.parser.syntax.imports.ImportExtra;

/**
 * @author dawn
 */
public class ImportExtraChannel extends ImportUtilChannel<ImportExtra> {

    private ImportExtraChannel(Channel channel, Kind... end) {
        super(channel, end);
        if (glance().not(Kind.EXTRA)) {
            panic("must be " + Kind.EXTRA.value, glance());
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
            case "EXTRA": acceptSpaces().accept(Kind.LR).refuseAll(); break;
            case "EXTRA SPACES": removeLast();
                accept(Kind.ID).accept(Kind.LR).refuseAll(); break;
            case "EXTRA ID": removeLast(); recover();
                addLast(ImportAsChannel.make(this, Kind.CR));
                over(this::full).refuseAll(); break;
            case "EXTRA LR": acceptSpaces().accept(Kind.CR).accept(Kind.ID).refuseAll(); break;
            case "EXTRA LR SPACES": removeLast(); accept(Kind.CR).accept(Kind.ID).refuseAll(); break;
            case "EXTRA LR CR": removeLast(); acceptSpaces().accept(Kind.CR).accept(Kind.ID).refuseAll(); break;
            case "EXTRA LR ID": removeLast(); recover();
                LR = (Token) removeLast();
                addLast(ImportAsChannel.make(this, Kind.COMMA, Kind.RR));
                addOthers();
                dropSpacesCR();
                if (glance().not(Kind.RR)) {
                    panic("must be )", glance());
                }
                RR = next();
                over(this::full).refuseAll(); break;
            default: panic("wrong " + Kind.EXTRA.value + " statement.");
        }
    }

    private void full() {
        Token symbol = (Token) removeFirst();
        ImportAs first = (ImportAs) removeFirst();
        if (channelFull()) {
            panic("wrong " + Kind.EXTRA.value + " statement", symbol);
        }
        data = new ImportExtra(symbol, LR, first, others, RR);
    }

    static ImportExtra make(Channel channel, Kind... end) {
        return new ImportExtraChannel(channel, end).make();
    }

}
