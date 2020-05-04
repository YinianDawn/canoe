package canoe.parser.syntax.statement.loop;

import canoe.ast.statement.Statement;
import canoe.lexer.Token;

/**
 * @author dawn
 */
public class StatementLoopLabel implements Statement {

    private Token sign;

    private Token label;

    public StatementLoopLabel(Token sign, Token label) {
        this.sign = sign;
        this.label = label;
    }

    @Override
    public Token first() {
        return sign;
    }

    @Override
    public Token last() {
        return label;
    }
}
