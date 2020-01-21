package canoe.parser.channel;

import canoe.ast.statement.Statement;
import canoe.ast.statement.Statements;
import canoe.lexer.Kind;
import canoe.parser.TokenStream;
import canoe.parser.channel.statement.StatementChannel;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dawn
 */
public class StatementsChannel extends Channel {

    private List<Statement> statements = new LinkedList<>();

    private int times;

    public StatementsChannel(String name, TokenStream stream, int times, Kind... end) {
        super(name, stream, end);
        this.times = times;
    }

    public Statements get() {
        while (!full()) { eat(); }
        return new Statements(statements);
    }

    private boolean full() {
        if (0 < times && times <= statements.size()) { return true; }
        return end(glance());
    }

    private void eat() {
        statements.add(new StatementChannel(this, Kind.CR).get());
        removeSpaceOrCR();
    }

}
