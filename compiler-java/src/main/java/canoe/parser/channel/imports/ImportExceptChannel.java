package canoe.parser.channel.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.imports.ImportAs;
import canoe.parser.syntax.imports.ImportExcept;

/**
 * @author dawn
 */
public class ImportExceptChannel extends ImportUtilChannel<ImportExcept> {

    private ImportExceptChannel(Channel channel, Kind... end) {
        super(channel, end);
        if (glance().not(Kind.EXCEPT)) {
            panic("must be " + Kind.EXCEPT.value, glance());
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
            case "EXCEPT": acceptSpaces().accept(Kind.LR).refuseAll(); break;
            case "EXCEPT SPACES": removeLast();
                accept(Kind.ID).accept(Kind.LR).refuseAll(); break;
            case "EXCEPT ID": removeLast(); recover();
                addLast(ImportAsChannel.make(this, Kind.CR));
                over(this::full).refuseAll(); break;
            case "EXCEPT LR": acceptSpaces().accept(Kind.CR).accept(Kind.ID).refuseAll(); break;
            case "EXCEPT LR SPACES": removeLast(); accept(Kind.CR).accept(Kind.ID).refuseAll(); break;
            case "EXCEPT LR CR": removeLast(); acceptSpaces().accept(Kind.CR).accept(Kind.ID).refuseAll(); break;
            case "EXCEPT LR ID": removeLast(); recover();
                LR = (Token) removeLast();
                addLast(ImportAsChannel.make(this, Kind.COMMA, Kind.RR));
                addOthers();
                dropSpacesCR();
                if (glance().not(Kind.RR)) {
                    panic("must be )", glance());
                }
                RR = next();
                over(this::full).refuseAll(); break;
            default: panic("wrong " + Kind.EXCEPT.value + " statement.");
        }
    }

    private void full() {
        Token symbol = (Token) removeFirst();
        ImportAs first = (ImportAs) removeFirst();
        if (channelFull()) {
            panic("wrong " + Kind.EXCEPT.value + " statement", symbol);
        }
        data = new ImportExcept(symbol, LR, first, others, RR);
    }

    static ImportExcept make(Channel channel, Kind... end) {
        return new ImportExceptChannel(channel, end).make();
    }

}
