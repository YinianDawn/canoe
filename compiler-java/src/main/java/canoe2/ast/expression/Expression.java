package canoe2.ast.expression;

import canoe2.lexis.Kind;

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
