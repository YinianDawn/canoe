package canoe.ast.imports;

import canoe.lexer.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dawn
 */
public class ImportStatementSingle implements ImportStatement {

    private Token importToken;
    private List<Token> importNames;
    private Token as;
    private Token id;

    private String names;
    private String name;

    public ImportStatementSingle(Token importToken, List<Token> importNames, Token as, Token id) {
        this.importToken = importToken;
        this.importNames = importNames;
        this.as = as;
        this.id = id;

        this.names = importNames.stream().map(Token::getValue).collect(Collectors.joining());
        this.name = null == id ? importNames.get(importNames.size() - 1).getValue() : id.getValue();
    }
}
