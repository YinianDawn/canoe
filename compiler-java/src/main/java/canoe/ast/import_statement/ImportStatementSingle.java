package canoe.ast.import_statement;

import canoe.lexis.Token;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dawn
 */
public class ImportStatementSingle extends ImportStatement {
    private Token importToken;
    private List<Token> importNames;
    private Token as;
    private Token id;

    private String names;

    public ImportStatementSingle(Token importToken, List<Token> importNames, Token as, Token id) {
        this.importToken = importToken;
        this.importNames = importNames;
        this.as = as;
        this.id = id;

        if (null == id) {
            this.id = importNames.get(importNames.size() - 2);
        }

        this.names = importNames.stream().map(Token::getValue).collect(Collectors.joining());
    }
}
