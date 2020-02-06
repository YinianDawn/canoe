package canoe.parser.syntax.imports;

import canoe.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dawn
 */
public class ImportId {

    private final Token id;
    private final List<Token> info;

    public ImportId(Token id, List<Token> info) {
        this.id = id;
        this.info = info;
    }

    @Override
    public String toString() {
        return (null == id ? "" : id.value() + " ") + info.stream().map(Token::value).collect(Collectors.joining());
    }
}
