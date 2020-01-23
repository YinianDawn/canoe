package canoe.lexer;

/**
 * @author dawn
 */
public enum KindType {

    /**
     * 关键词 字母数字
     */
    KEY_WORD,

    /**
     * 逻辑运算符
     */
    SIGN_LOGICAL,

    /**
     * 关系运算符
     */
    SIGN_RELATION,

    /**
     * lambda表达式
     */
    SIGN_LAMBDA,

    /**
     * 赋值运算符
     */
    SIGN_ASSIGN,

    /**
     * 数学运算符
     */
    SIGN_MATH,

    /**
     * 数学运算符
     */
    SIGN_BIT,

    /**
     * 自增自减运算符
     */
    SIGN_SELF,

    /**
     * 特殊标记
     */
    SIGN_MARK,

    /**
     * 常量
     */
    CONSTANT,

    /**
     * 注释
     */
    COMMENT,

    /**
     * 界限符 分隔两个同类
     */
    MIDDLE,

    /**
     * 必须前面某个对象 不能单独存在
     */
    FOLLOW,

    /**
     * 必须后面某个对象 不能单独存在
     */
    FIRST,

    /**
     * 配对左边
     */
    COUPLE_LEFT,

    /**
     * 配对右边
     */
    COUPLE_RIGHT,

}
