package canoe.parser.syntax.statement;

import canoe.lexer.Token;

/**
 * @author dawn
 */
public interface Statement {

    /**
     * 第一个位置
     * @return
     */
    Token first();


    /**
     * 最后一个
     * @return
     */
    Token last();

    /**
     * 获取定义的名称
     * @return
     */
    String getId();
}
