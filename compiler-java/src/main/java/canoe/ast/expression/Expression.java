package canoe.ast.expression;

import canoe.lexis.Kind;

/**
 * @author dawn
 */
public interface Expression {

    /**
     * 是否以某类型结尾
     * @param kind
     * @return
     */
    boolean endWith(Kind kind);

}
