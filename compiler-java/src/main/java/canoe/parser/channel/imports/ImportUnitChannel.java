package canoe.parser.channel.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.imports.ImportExclude;
import canoe.parser.syntax.imports.ImportInclude;
import canoe.parser.syntax.imports.ImportUnit;

/**
 * @author dawn
 */
public class ImportUnitChannel extends Channel<ImportUnit> {

    private ImportInclude include = null;
    private ImportExclude exclude = null;

    private ImportUnitChannel(Channel channel, Kind... end) {
        super(channel, end);
        switch (glance().kind) {
            case UL:
            case MUL:
            case ID:
            case STRING: break;
            default: panic("wrong " + Kind.IMPORT.value + " statement", glance());
        }
        accept(Kind.UL, Kind.MUL, Kind.ID, Kind.STRING).refuseAll();
        init();
    }

    @Override
    protected boolean eat(Token next) {
        switch (next.kind) {
            case INCLUDE:
            case EXCLUDE:
                mark();
                break;
            default:
        }
        return true;
    }

    @Override
    protected void digest() {
        String status = status();
        switch (status) {
            case "MUL": acceptSpaces().refuseAll(); break;
            case "MUL SPACES": removeLast();
                accept(Kind.STRING).refuseAll(); break;
            case "MUL STRING":
            case "STRING": over(this::full).acceptSpaces().refuseAll(); break;
            case "STRING SPACES": removeLast();
                over(this::full).accept(Kind.INCLUDE).refuseAll(); break;

//            case "STRING EXTRA": removeLast(); recover();
//                extra = ImportExtraChannel.make(this, Kind.CR);
//                over(this::full).refuseAll(); break;

            default: panic("wrong " + Kind.IMPORT.value + " statement");
        }
    }

    private void full() {
        String status = status();
        switch (status) {
            case "MUL STRING":
                data = new ImportUnit((Token) removeFirst(), (Token) removeFirst(), null, null);
                return;
            default:
        }
        panic("wrong " + Kind.IMPORT.value + " statement: " + status);
    }

    static ImportUnit make(Channel channel, Kind... end) {
        return new ImportUnitChannel(channel, end).make();
    }

}
