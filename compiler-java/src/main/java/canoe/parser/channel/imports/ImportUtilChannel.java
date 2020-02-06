package canoe.parser.channel.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.imports.ImportId;
import canoe.parser.syntax.imports.ImportCommaId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dawn
 */
class ImportUtilChannel<T> extends Channel<T> {

    Token LR = null;
    List<ImportCommaId> others = new ArrayList<>();
    Token RR = null;

    ImportUtilChannel(Channel channel, Kind... ends) {
        super(channel, ends);
    }

    void addOthers() {
        while (glanceSkipSpaces().is(Kind.COMMA)) {
            dropSpaces();
            Token comma = next();
            dropSpacesCR();
            ImportId importAs = ImportIdChannel.make(this, Kind.COMMA, Kind.RR);
            others.add(new ImportCommaId(comma, importAs));
        }
    }
}
