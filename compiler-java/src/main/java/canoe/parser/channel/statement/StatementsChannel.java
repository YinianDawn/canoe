package canoe.parser.channel.statement;

import canoe.ast.statement.Statement;
import canoe.ast.statement.Statements;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.TokenStream;
import canoe.parser.channel.Channel;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dawn
 */
public class StatementsChannel extends Channel<Statements> {

    private List<Statement> statements = new LinkedList<>();

    private StatementsChannel(String name, TokenStream stream, Kind... end) {
        super(name, stream, end);
        removeSpaceOrCR();
        init();
    }

    @Override
    protected boolean eat(Token next) {
        statements.add(StatementChannel.produce(this, extend()));
        removeSpaceOrCR();
        if (end()) { data = new Statements(statements); }
        return false;
    }

    public static Statements produce(String name, TokenStream stream, Kind... end) {
        return new StatementsChannel(name, stream, end).produce();
    }

}
