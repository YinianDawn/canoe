package canoe.parser.channel.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.imports.ImportAs;
import canoe.parser.syntax.merge.MergeOperatorOverload;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dawn
 */
public class ImportAsChannel extends Channel<ImportAs> {

    private ImportAsChannel(Channel channel, Kind... end) {
        super(channel, end);
        if (glance().not(Kind.ID)) {
            panic("must be id string", glance());
        }
        init();
    }

    @Override
    protected void digest() {
        String status = status();

        switch (status) {
            case "ID": over(this::full).acceptSpaces().accept(Kind.DOT).refuseAll(); break;

            case "ID SPACES": removeLast(); over(this::full).accept(Kind.AS).refuseAll(); break;

            case "ID AS": acceptSpaces().refuseAll(); break;
            case "ID AS SPACES": removeLast(); accept(Kind.ID).refuseAll(); break;
            case "ID AS ID": over(this::full).acceptSpaces().refuseAll(); break;
            case "ID AS ID SPACES": removeLast(); over(this::full).refuseAll(); break;

            case "ID DOT": over(this::full).acceptSpaces().accept(MergeOperatorOverload.OPERATOR_OVERLOAD).refuseAll(); break;
            case "ID DOT SPACES": removeLast(); over(this::full).refuseAll(); break;
            case "ID DOT ADD": over(this::full).acceptSpaces().refuseAll(); break;
            case "ID DOT ADD SPACES": removeLast(); over(this::full).refuseAll(); break;
            default: panic("wrong import statement.");
        }
    }

    private void full() {
        List<Token> info = new ArrayList<>();
        Token as = null;
        Token id = null;
        if (3 <= channelSize()) {
            id = getLastToken(); removeLast();
            as = getLastToken(); removeLast();
            if (as.not(Kind.AS)) {
                addLast(as);
                addLast(id);
                as = null;
                id = null;
            }
        }
        while (channelFull()) { info.add((Token) removeFirst()); }
        data = new ImportAs(info, as, id);
    }

    static ImportAs make(Channel channel, Kind... end) {
        return new ImportAsChannel(channel, end).make();
    }

}
