package canoe.lexer;

/**
 * @author dawn
 */
public enum KindType {

    /** 关键词 字母数字 */ KEY_WORD,
    /** 关键词 基本类型 */ BASIC_TYPE,
    /** 特殊标记 */ MARK,
    /** 常量 */ CONSTANT,
    /** 变量 */ VARIANT,
    /** 注释 */ COMMENT,
    /** 操作符 */ OPERATOR,
    /** 重载 */ OVERLOAD,

    /** 关系运算符 */ OPERATOR_RELATION,
    /** 逻辑运算符 */ OPERATOR_LOGIC,
    /** 数学运算符 */ OPERATOR_MATH,
    /** 位运算符 */ OPERATOR_BIT,
    /** 自增自减 */ OPERATOR_SELF,
    /** 赋值运算符 */ OPERATOR_ASSIGN,

    /** 界限符 分隔两个同类 */ MIDDLE,
    /** 必须前面某个对象 不能单独存在 */ RIGHT,
    /** 必须后面某个对象 不能单独存在 */ LEFT,
    /** 配对左边 */ COUPLE_LEFT,
    /** 配对右边 */ COUPLE_RIGHT,

}
