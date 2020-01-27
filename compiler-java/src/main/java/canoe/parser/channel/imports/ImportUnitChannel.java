package canoe.parser.channel.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.imports.ImportExcept;
import canoe.parser.syntax.imports.ImportExtra;
import canoe.parser.syntax.imports.ImportUnit;

/**
 * @author dawn
 */
public class ImportUnitChannel extends Channel<ImportUnit> {

    private Token AS = null;
    private Token id = null;
    private ImportExtra extra = null;
    private ImportExcept except = null;

    private ImportUnitChannel(Channel channel, Kind... end) {
        super(channel, end);
        if (glance().not(Kind.STRING)) {
            panic("must be path string", glance());
        }
        init();
    }

    @Override
    protected boolean eat(Token next) {
        switch (next.kind) {
            case EXTRA:
            case EXCEPT:
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
            case "STRING": over(this::full).acceptSpaces().refuseAll(); break;
            case "STRING SPACES": removeLast();
                over(this::full).accept(Kind.AS, Kind.EXTRA, Kind.EXCEPT).refuseAll(); break;
            case "STRING AS": acceptSpaces().refuseAll(); break;
            case "STRING AS SPACES": removeLast(); accept(Kind.ID, Kind.DOT, Kind.UL).refuseAll(); break;
            case "STRING AS UL":
            case "STRING AS DOT":
            case "STRING AS ID": over(this::full).acceptSpaces().refuseAll(); break;
            case "STRING AS UL SPACES":
            case "STRING AS ID SPACES": removeLast();
                over(this::full).accept(Kind.EXTRA).refuseAll(); break;
            case "STRING AS DOT SPACES": removeLast();
                over(this::full).accept(Kind.EXCEPT).refuseAll(); break;
            case "STRING AS UL EXTRA":
            case "STRING AS ID EXTRA":
            case "STRING EXTRA": removeLast(); recover();
                extra = ImportExtraChannel.make(this, Kind.CR);
                over(this::full).refuseAll(); break;
            case "STRING AS DOT EXCEPT": removeLast(); recover();
                except = ImportExceptChannel.make(this, Kind.CR);
                over(this::full).refuseAll(); break;
            default: panic("wrong " + Kind.IMPORT.value + " statement");
        }
    }

    private void full() {
        Token path = (Token) removeFirst();
        if (channelFull()) {
            Object next = removeFirst();
            if (next instanceof Token) {
                AS = (Token) next;
                id = (Token) removeFirst();
                if (channelFull()) {
                    next = removeFirst();
                } else {
                    next = null;
                }
            }
            if (null != next) {
                if (next instanceof ImportExtra) {
                    extra = (ImportExtra) next;
                } else if (next instanceof ImportExcept) {
                    except = (ImportExcept) next;
                }
                if (channelFull()) {
                    panic("wrong import statement.", null == id ? path : id);
                }
            }
        }
        data = new ImportUnit(path, AS, id, extra, except);
    }

    static ImportUnit make(Channel channel, Kind... end) {
        return new ImportUnitChannel(channel, end).make();
    }

}
