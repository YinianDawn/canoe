package canoe.parser.syntax.imports;

import canoe.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dawn
 */
public class ImportAs {

    private final List<Token> info;
    private final Token AS;
    private final Token id;

    public ImportAs(List<Token> info, Token symbol, Token id) {
        this.info = info;
        this.AS = symbol;
        this.id = id;
    }

    @Override
    public String toString() {
        return info.stream().map(Token::value).collect(Collectors.joining()) + (null == AS ? "" : (" " + AS.value() + " " + id.value()));
    }
}
