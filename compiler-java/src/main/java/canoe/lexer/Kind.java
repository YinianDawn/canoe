package canoe.lexer;

import static canoe.lexer.KindType.*;

/**
 * @author dawn
 */
public enum Kind {

    // ================ 关键字 ================

    /** 包声明 */
    PACKAGE("package", KEY_WORD),

    /** 类型或函数导入 */
    IMPORT("import", KEY_WORD),
    /** 类型或函数导入重命名 */
    AS("as", KEY_WORD),

    /** 可见性 */
    OPEN("open", KEY_WORD),

    /** 程序原生函数 */
    NATIVE("native", KEY_WORD),

    /** goto标签或者跳转至goto标签语句 */
    GOTO("goto", KEY_WORD),

    /** 枚举类型 */
    ENUM("enum", KEY_WORD),

    /** 新线程 TODO 多线程怎么交互？ */
    CANOE("canoe", KEY_WORD),

    /** 返回语句关键词 */
    RETURN("return", KEY_WORD),

    // ================ 流程控制语句 ================

    /** if语句 */
    IF("if", KEY_WORD),
    /** else语句 */
    ELSE("else", KEY_WORD),
    /** else if语句 */
    ELSE_IF("else if", KEY_WORD),

    /** match语句 */
    MATCH("match", KEY_WORD),
    /** 若是标签 表明本match默认连续执行 */
    WITH("with", KEY_WORD),
    /** 若是标签 表明本match默认不连续执行 与with相反 并且without是默认属性 */
    WITHOUT("without", KEY_WORD),
    /** in 类似二元操作符 表是前面是否属于后面的 */
    IN("in", KEY_WORD),

    /** loop循环语句 loop 后面也可以有语句 第一次时执行一次 */
    LOOP("loop", KEY_WORD),
    /** 跳过本层循环 或 跳过指定标签循环 */
    BREAK("break", KEY_WORD),
    /** 继续本层循环 或 继续指定标签循环 */
    CONTINUE("continue", KEY_WORD),
    /** each循环语句  each ... in ... each语句是遍历的意思 */
    EACH("each", KEY_WORD),
    /** for循环语句  for ;; 语句还是要有 确实好用的 */
    FOR("for", KEY_WORD),

    // ================ 其他关键字 ================

    /** 布尔常量值 true */
    TRUE("true", KEY_WORD, CONSTANT),
    /** 布尔常量值 false */
    FALSE("false", KEY_WORD, CONSTANT),

    /** 基本类型 */
    INT8("int8", KEY_WORD),
    INT16("int16", KEY_WORD),
    INT32("int32", KEY_WORD),
    INT64("int64", KEY_WORD),
    FLOAT32("float32", KEY_WORD),
    FLOAT64("float64", KEY_WORD),

    // ================ 符号 ================

    // 逻辑运算符
    /** 逻辑与 */
    LOGICAL_AND("&&", MIDDLE, SIGN_LOGICAL),
    /** 逻辑或 */
    LOGICAL_OR("||", MIDDLE, SIGN_LOGICAL),
    /** 逻辑非 与位取反相同保留位取反操作符 */
//    LOGICAL_NOT("!"),

    // 关系运算符
    /** 是否相等 */
    EQ("==", MIDDLE, SIGN_RELATION),
    /** 是否不等 */
    NE("!=", MIDDLE, SIGN_RELATION),
    /** 是否大于 */
    GT(">", MIDDLE, SIGN_RELATION, COUPLE_RIGHT),
    /** 是否大于等于 */
    GE(">=", MIDDLE, SIGN_RELATION),
    /** 是否小于 */
    LT("<", MIDDLE, SIGN_RELATION, COUPLE_LEFT),
    /** 是否小于等于 */
    LE("<=", MIDDLE, SIGN_RELATION),
    /** 前面对象是否是后面的类型 */
    IS("<-", MIDDLE, SIGN_RELATION),

    /** lambda表达式 */
    LAMBDA("->", MIDDLE, SIGN_LAMBDA),

    /** 赋值 不改变约束 */
    ASSIGN("=", MIDDLE, SIGN_ASSIGN),
    /** 强制赋值 可以改变约束 */
    ASSIGN_FORCE(":=", MIDDLE, SIGN_ASSIGN),

    /** 包名可能有 指明属性(当前对象的) */
    DOT(".", MIDDLE),
    /** 指明属性(上个对象的) */
    DOT_DOT("..", MIDDLE),
    /** 函数输入可变长度 必须最后一个参数才允许 */
    DOT_DOT_DOT("...", FOLLOW, SIGN_MARK),

    // 范围界限符号 成对出现 <> 是泛型界限，已经大于小于符号了
    LB("{", COUPLE_LEFT),
    RB("}", COUPLE_RIGHT),
    LS("[", COUPLE_LEFT),
    RS("]", COUPLE_RIGHT),
    LR("(", COUPLE_LEFT),
    RR(")", COUPLE_RIGHT),

    /** 描述属性 match子句  */
    COLON(":", FIRST, SIGN_MARK),
    COLON_BLANK(": ", SIGN_MARK),

    /** 多个函数参数分隔 数组元素分隔 */
    COMMA(",", MIDDLE, SIGN_MARK),

    /** 语句之间分隔 换行符也是语句分隔作用 */
    SEMI(";", MIDDLE, SIGN_MARK),
    /** 暂时无用 绑定对象用 */
    AT("@", FIRST, SIGN_MARK),
    /** 暂时无用 也许将来注解使用 */
    HASH("#", FIRST, SIGN_MARK),

    // 数学运算符
    ADD("+", FIRST, MIDDLE, SIGN_MATH),
    SUB("-", FIRST, MIDDLE, SIGN_MATH),
    MUL("*", MIDDLE, SIGN_MATH),
    DIV("/", MIDDLE, SIGN_MATH),
    MOD("%", MIDDLE, SIGN_MATH),

    // 位运算符
    BIT_AND("&", MIDDLE, SIGN_BIT),
    BIT_OR("|", MIDDLE, SIGN_BIT),
    BIT_XOR("^", MIDDLE, SIGN_BIT),
    BIT_NOT("!", FIRST, SIGN_LOGICAL, SIGN_BIT),
    BIT_LEFT("<<", MIDDLE, SIGN_BIT),
    BIT_RIGHT(">>", MIDDLE, SIGN_BIT),

    // 自增自减运算
    ADD_ADD("++", FOLLOW, SIGN_SELF),
    SUB_SUB("--", FOLLOW, SIGN_SELF),

    // 运算并赋值
    ADD_ASSIGN("+=", MIDDLE, SIGN_ASSIGN),
    SUB_ASSIGN("-=", MIDDLE, SIGN_ASSIGN),
    MUL_ASSIGN("*=", MIDDLE, SIGN_ASSIGN),
    DIV_ASSIGN("/=", MIDDLE, SIGN_ASSIGN),
    MOD_ASSIGN("%=", MIDDLE, SIGN_ASSIGN),

    BIT_AND_ASSIGN("&=", MIDDLE, SIGN_ASSIGN),
    BIT_OR_ASSIGN("|=", MIDDLE, SIGN_ASSIGN),
    BIT_XOR_ASSIGN("^=", MIDDLE, SIGN_ASSIGN),
    BIT_LEFT_ASSIGN("<<=", MIDDLE, SIGN_ASSIGN),
    BIT_RIGHT_ASSIGN(">>=", MIDDLE, SIGN_ASSIGN),

    // ================ 无法枚举的符号 ================

    /** 标识符 [A-Za-z_][A-Za-z0-9_]* */
    ID(null),

    // 数字
    /** 十六进制数 0(x|X)[0-9a-fA-F_]* */
    NUMBER_HEX(null, CONSTANT),
    /** 十进制数 [1-9][0-9_]* */
    NUMBER_DEC(null, CONSTANT),
    /** 八进制数 0[1-7][0-7_]* */
    NUMBER_OCT("NUMBER_OCT", CONSTANT),
    /** 二进制数 0(b|B)[0-1_]* */
    NUMBER_BIN(null, CONSTANT),

    /** 十进制小数 [0-9_]*\.[0-9_]+ */
    DECIMAL(null, CONSTANT),

    /** 字符串 \"[.\n]*\" */
    STRING(null, CONSTANT),

    /** 行注释 // */
    COMMENT_LINE(null, COMMENT),
    /** 块注释 /* */
    COMMENT_BLOCK(null, COMMENT),

    /** 换行符 */
    CR("<\\n>", SIGN_MARK),
    /** 空格 */
    BLANK("< >", SIGN_MARK),
    /** 空白 */
    SPACES("<SPACES>", SIGN_MARK),
    /** 文件结束 */
    EOF("<EOF>", SIGN_MARK);

    public final String sign;

    public final KindType[] types;

    Kind(String sign, KindType... types) {
        this.sign = sign;
        this.types = types;
    }

}
