package canoe.parser.channel.statement;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.TokenStream;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.Statements;
import canoe.parser.syntax.statement.Statement;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dawn
 */
public class StatementsChannel extends Channel<Statements> {

    private List<Statement> statements = new LinkedList<>();

    private StatementsChannel(TokenStream stream, Kind... end) {
        super(stream, end);
        init();
    }

    @Override
    protected boolean eat(Token next) {
        statements.add(StatementChannel.make(this, extend(Kind.CR, Kind.SEMI)));
        dropSpacesSemiCR();
        if (over(glance())) { data = new Statements(statements); }
        return false;
    }

    public static Statements make(TokenStream stream, Kind... end) {
        return new StatementsChannel(stream, end).make();
    }

}
