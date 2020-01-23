package canoe.ast.statement;

import canoe.ast.expression.ExpressionComma;
import canoe.lexer.Token;

import java.util.List;

/**
 * @author dawn
 */
public class StatementComma implements Statement {

    private Statement statement;

    private List<ExpressionComma> commas;

    public StatementComma(Statement statement, List<ExpressionComma> commas) {
        this.statement = statement;
        this.commas = commas;
    }

    @Override
    public Token first() {
        return statement.first();
    }

    @Override
    public Token last() {
        return commas.isEmpty() ? statement.last() : commas.get(commas.size() - 1).last();
    }
}
