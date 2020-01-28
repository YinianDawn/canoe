package canoe.parser.syntax.statement;

import canoe.lexer.Token;

import java.util.List;

/**
 * @author dawn
 */
public class StatementComma implements Statement {

    private List<Statement> statements;
    private List<Token> commas;

    public StatementComma(List<Statement> statements, List<Token> commas) {
        this.statements = statements;
        this.commas = commas;
    }

    @Override
    public Token first() {
        return statements.get(0).first();
    }

    @Override
    public Token last() {
        return statements.get(statements.size() - 1).last();
    }
}
