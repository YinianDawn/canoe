package canoe.parser.channel.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.imports.ImportId;
import canoe.parser.syntax.imports.ImportExclude;

/**
 * @author dawn
 */
public class ImportExcludeChannel extends ImportUtilChannel<ImportExclude> {

    private ImportExcludeChannel(Channel channel, Kind... end) {
        super(channel, end);
        if (glance().not(Kind.EXCLUDE)) {
            panic("must be " + Kind.EXCLUDE.value, glance());
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
            case "EXCLUDE": acceptSpaces().accept(Kind.LR).refuseAll(); break;
            case "EXCLUDE SPACES": removeLast();
                accept(Kind.ID).accept(Kind.LR).refuseAll(); break;
            case "EXCLUDE ID": removeLast(); recover();
                addLast(ImportIdChannel.make(this, Kind.CR));
                over(this::full).refuseAll(); break;
            case "EXCLUDE LR": acceptSpaces().accept(Kind.CR).accept(Kind.ID).refuseAll(); break;
            case "EXCLUDE LR SPACES": removeLast(); accept(Kind.CR).accept(Kind.ID).refuseAll(); break;
            case "EXCLUDE LR CR": removeLast(); acceptSpaces().accept(Kind.CR).accept(Kind.ID).refuseAll(); break;
            case "EXCLUDE LR ID": removeLast(); recover();
                LR = (Token) removeLast();
                addLast(ImportIdChannel.make(this, Kind.COMMA, Kind.RR));
                addOthers();
                dropSpacesCR();
                if (glance().not(Kind.RR)) {
                    panic("must be )", glance());
                }
                RR = next();
                over(this::full).refuseAll(); break;
            default: panic("wrong " + Kind.EXCLUDE.value + " statement.");
        }
    }

    private void full() {
        Token symbol = (Token) removeFirst();
        ImportId first = (ImportId) removeFirst();
        if (channelFull()) {
            panic("wrong " + Kind.EXCLUDE.value + " statement", symbol);
        }
        data = new ImportExclude(symbol, LR, first, others, RR);
    }

    static ImportExclude make(Channel channel, Kind... end) {
        return new ImportExcludeChannel(channel, end).make();
    }

}
