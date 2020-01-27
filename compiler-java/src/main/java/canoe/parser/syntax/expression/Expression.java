package canoe.parser.syntax.expression;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public interface Expression {

    /**
     * 第一个
     * @return
     */
    Token first();

    /**
     * 最后一个
     * @return
     */
    Token last();

}
