package canoe.parser.syntax;

import canoe.parser.syntax.statement.Statement;
import canoe.util.Dump;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author dawn
 */
public class Statements implements Dump {

    private List<Statement> statements;

    public Statements(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public void dump(Consumer<String> print) {
        statements.forEach(s -> print.accept(s.toString()));
    }
}
