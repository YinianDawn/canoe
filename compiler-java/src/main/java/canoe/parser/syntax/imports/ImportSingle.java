package canoe.parser.syntax.imports;

import canoe.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dawn
 */
public class ImportStatementSingle implements ImportStatement {

    private Token symbol;
    private List<Token> info;
    private Token as;
    private Token id;

    private String names;
    private String name;

    public ImportStatementSingle(Token symbol, List<Token> info, Token as, Token id) {
        this.symbol = symbol;
        this.info = info;
        this.as = as;
        this.id = id;

        this.names = info.stream().map(Token::value).collect(Collectors.joining());
        this.name = null == id ? info.get(info.size() - 1).value() : id.value();
    }
}
