package canoe.parser.channel.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.imports.ImportAs;
import canoe.parser.syntax.imports.ImportCommaAs;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dawn
 */
class ImportUtilChannel<T> extends Channel<T> {

    Token LR = null;
    List<ImportCommaAs> others = new ArrayList<>();
    Token RR = null;

    ImportUtilChannel(Channel channel, Kind... ends) {
        super(channel, ends);
    }

    void addOthers() {
        while (glanceSkipSpaces().is(Kind.COMMA)) {
            dropSpaces();
            Token comma = next();
            dropSpacesCR();
            ImportAs importAs = ImportAsChannel.make(this, Kind.COMMA, Kind.RR);
            others.add(new ImportCommaAs(comma, importAs));
        }
    }
}
