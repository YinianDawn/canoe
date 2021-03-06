package canoe.lexer;

import canoe.util.PanicUtil;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static canoe.lexer.KindType.*;

/**
 * @author dawn
 */
public enum Kind {

    // ================ 关键字 ================

    PACKAGE("package", KEY_WORD),

    IMPORT ("import",  KEY_WORD),
    INCLUDE("include", KEY_WORD),
    EXCLUDE("exclude", KEY_WORD),

    /** 包外可见 */ OPENED("opened", KEY_WORD),
    /** 文件可见 */ CLOSED("closed", KEY_WORD),
    /** 本地方法 */ NATIVE("native", KEY_WORD),
    /** 静态函数 */ STATIC("static", KEY_WORD),
    /** 单例类型 */ UNIQUE("unique", KEY_WORD),
    /** 枚举类型 */ ENUM  ("enum",   KEY_WORD),
    /** 懒惰加载 */ LAZY  ("lazy",   KEY_WORD),

    GOTO  ("goto",   KEY_WORD),
    RETURN("return", KEY_WORD),

    /**
     * // 这是一个函数
     * // 调用需要传入int类型
     * // 返回bool值
     * // 如果作为新启动线程的话 可以传入string类型
     * relay :(index :int) :canoe:int :cargo:string :bool {
     *     // 获取接受者对象 问题是不知道接受者线程接受什么类型
     *     canoes = canoe()
     *     // 获取传入的值
     *     cargo = canoed()
     *     canoe
     * }
     * canoe<I,O> :(:() :canoe(:O) :cargo:I, canoes... :cargo:O) {
     *
     * }
     *
     * canoe1 := canoe<bool>(() -> relay(5))
     *
     * canoe3 = canoe(other, canoe1, canoe2) 以本线程为名 启动一个新线程
     * canoe3.canoe("123")
     */
    CANOE ("canoe",  KEY_WORD),
    CANOED("canoed",  KEY_WORD),

    // ================ 流程控制语句 ================

    IF     ("if",      KEY_WORD),
    ELSE   ("else",    KEY_WORD),
    ELSE_IF("else if", KEY_WORD),

    MATCH  ("match",   KEY_WORD),
    WITH   ("with",    KEY_WORD),
    WITHOUT("without", KEY_WORD),

    LOOP    ("loop",     KEY_WORD),
    BREAK   ("break",    KEY_WORD),
    CONTINUE("continue", KEY_WORD),
    EACH    ("each",     KEY_WORD),
    FOR     ("for",      KEY_WORD),

    // ================ 其他关键字 ================

    TRUE ("true",  KEY_WORD, CONSTANT),
    FALSE("false", KEY_WORD, CONSTANT),
    NIL  ("nil",   KEY_WORD, CONSTANT),

    /** 基本类型 */
    BOOL   ("bool",    KEY_WORD, BASIC_TYPE),
    UINT8  ("uint8",   KEY_WORD, BASIC_TYPE),
    UINT16 ("uint16",  KEY_WORD, BASIC_TYPE),
    UINT32 ("uint32",  KEY_WORD, BASIC_TYPE),
    UINT64 ("uint64",  KEY_WORD, BASIC_TYPE),
    INT8   ("int8",    KEY_WORD, BASIC_TYPE),
    INT16  ("int16",   KEY_WORD, BASIC_TYPE),
    INT32  ("int32",   KEY_WORD, BASIC_TYPE),
    INT64  ("int64",   KEY_WORD, BASIC_TYPE),
    FLOAT32("float32", KEY_WORD, BASIC_TYPE),
    FLOAT64("float64", KEY_WORD, BASIC_TYPE),

    // ================ 符号 ================

    // 关系运算符
    /** 是否相等 */ EQ("==",  OPERATOR, OPERATOR_RELATION, MIDDLE, OVERLOAD),
    /** 是否不等 */ NE("!=",  OPERATOR, OPERATOR_RELATION, MIDDLE, OVERLOAD),
    /** 是否大于 */ GT(">",   OPERATOR, OPERATOR_RELATION, MIDDLE, OVERLOAD, COUPLE_RIGHT),
    /** 是否大等 */ GE(">=",  OPERATOR, OPERATOR_RELATION, MIDDLE, OVERLOAD),
    /** 是否小于 */ LT("<",   OPERATOR, OPERATOR_RELATION, MIDDLE, OVERLOAD, COUPLE_LEFT),
    /** 是否小等 */ LE("<=",  OPERATOR, OPERATOR_RELATION, MIDDLE, OVERLOAD),

    /** 是否对象 */ ET("?",   OPERATOR, OPERATOR_RELATION, RIGHT),
    /** 是非对象 */ NT("?!",  OPERATOR, OPERATOR_RELATION, RIGHT),
    /** 是否实例 */ IS("?:",  OPERATOR, OPERATOR_RELATION, MIDDLE),
    /** 是非实例 */ NO("?!:", OPERATOR, OPERATOR_RELATION, MIDDLE),
    /** 是否属于 */ BL("?<",  OPERATOR, OPERATOR_RELATION, MIDDLE),
    /** 是否不属 */ NB("?!<", OPERATOR, OPERATOR_RELATION, MIDDLE),

    // 逻辑运算符
    /** 逻辑与 */ LOGIC_AND("&&", OPERATOR, OPERATOR_LOGIC, MIDDLE, OVERLOAD),
    /** 逻辑或 */ LOGIC_OR ("||", OPERATOR, OPERATOR_LOGIC, MIDDLE, OVERLOAD),
    /** 逻辑非 */ LOGIC_NOT("!",  OPERATOR, OPERATOR_LOGIC, LEFT),

    // 数学运算符
    ADD("+", OPERATOR, OPERATOR_MATH, MIDDLE, LEFT, OVERLOAD),
    SUB("-", OPERATOR, OPERATOR_MATH, MIDDLE, LEFT, OVERLOAD),
    MUL("*", OPERATOR, OPERATOR_MATH, MIDDLE, OVERLOAD),
    DIV("/", OPERATOR, OPERATOR_MATH, MIDDLE, OVERLOAD),
    MOD("%", OPERATOR, OPERATOR_MATH, MIDDLE, OVERLOAD),
    EXP("^", OPERATOR, OPERATOR_MATH, MIDDLE, OVERLOAD),

    // 位运算符
    BIT_AND       ("&",   OPERATOR, OPERATOR_BIT, MIDDLE, OVERLOAD),
    BIT_OR        ("|",   OPERATOR, OPERATOR_BIT, MIDDLE, OVERLOAD),
    BIT_XOR       ("&|",  OPERATOR, OPERATOR_BIT, MIDDLE, OVERLOAD),
    BIT_XOR2      ("|&",  OPERATOR, OPERATOR_BIT, MIDDLE, OVERLOAD),
    BIT_NOT       ("~",   OPERATOR, OPERATOR_BIT, LEFT),
    BIT_LEFT      ("<<",  OPERATOR, OPERATOR_BIT, MIDDLE, OVERLOAD),
    BIT_RIGHT     (">>",  OPERATOR, OPERATOR_BIT, MIDDLE, OVERLOAD),
    BIT_RIGHT_ZERO(">>>", OPERATOR, OPERATOR_BIT, MIDDLE, OVERLOAD),

    // 自增自减运算
    SELF_ADD("++", OPERATOR, OPERATOR_SELF, RIGHT),
    SELF_SUB("--", OPERATOR, OPERATOR_SELF, RIGHT),

    // 赋值
    ASSIGN      ("=",  OPERATOR, OPERATOR_ASSIGN),
    ASSIGN_FORCE(":=", OPERATOR, OPERATOR_ASSIGN),

    // 运算并赋值
    ASSIGN_ADD("+=", OPERATOR, OPERATOR_ASSIGN),
    ASSIGN_SUB("-=", OPERATOR, OPERATOR_ASSIGN),
    ASSIGN_MUL("*=", OPERATOR, OPERATOR_ASSIGN),
    ASSIGN_DIV("/=", OPERATOR, OPERATOR_ASSIGN),
    ASSIGN_MOD("%=", OPERATOR, OPERATOR_ASSIGN),
    ASSIGN_EXP("^=", OPERATOR, OPERATOR_ASSIGN),

    ASSIGN_BIT_AND       ("&=",   OPERATOR, OPERATOR_ASSIGN),
    ASSIGN_BIT_OR        ("|=",   OPERATOR, OPERATOR_ASSIGN),
    ASSIGN_BIT_XOR       ("&|=",  OPERATOR, OPERATOR_ASSIGN),
    ASSIGN_BIT_XOR2      ("|&=",  OPERATOR, OPERATOR_ASSIGN),
    ASSIGN_BIT_LEFT      ("<<=",  OPERATOR, OPERATOR_ASSIGN),
    ASSIGN_BIT_RIGHT     (">>=",  OPERATOR, OPERATOR_ASSIGN),
    ASSIGN_BIT_RIGHT_ZERO(">>>=", OPERATOR, OPERATOR_ASSIGN),

    DOT(".", OPERATOR, MIDDLE, LEFT),

    /** 函数输入可变长度 必须最后一个参数才允许 */
    DOT_DOT_DOT("...", OPERATOR, RIGHT),

    /** 方法返回值和lambda表达式 */
    POINT("->", OPERATOR, MIDDLE),

    /** each循环里面用 */
    IN("<-", OPERATOR, MIDDLE),

    /** 多个函数参数分隔 数组元素分隔 */
    COMMA(",", OPERATOR, MIDDLE),
    /** 语句之间分隔 换行符也是语句分隔作用 */
    SEMI (";", OPERATOR, MIDDLE),
    /** 描述约束等 */
    COLON(":",  OPERATOR, MIDDLE, LEFT),
    /** match子句 */
    COLON_BLANK(": ", OPERATOR, MIDDLE),
    BLANK_COLON_BLANK(" : ", OPERATOR, MIDDLE),

    // 范围界限符号 成对出现 <> 是泛型界限，已经大于小于符号了
    LB("{", COUPLE_LEFT), RB("}", COUPLE_RIGHT),
    LS("[", COUPLE_LEFT), RS("]", COUPLE_RIGHT),
    LR("(", COUPLE_LEFT), RR(")", COUPLE_RIGHT),

    /** 单个id是这个就是UL */
    UL("_", VARIANT),

    /** 暂时无用 */
    AT("@", OPERATOR, LEFT),
    /** 暂时无用 也许将来注解使用 */
    HASH("#", OPERATOR, LEFT),
    /** 暂时无用 */
    ANTI("`", OPERATOR),
    /** 暂时无用 */
    DOLLAR("$", OPERATOR),
    /** 暂时无用 */
    BACK("\\", OPERATOR),

    // ================ 无法枚举的符号 ================

    /** 标识符 [A-Za-z_][A-Za-z0-9_]* */
    ID(null, VARIANT),

    // 数字
    /** 十六进制数 0(x|X)[0-9a-fA-F_]* */
    NUMBER_HEX(null, CONSTANT),
    /** 十进制数 [1-9][0-9_]* */
    NUMBER_DEC(null, CONSTANT),
    /** 八进制数 0[1-7][0-7_]* */
    NUMBER_OCT(null, CONSTANT),
    /** 二进制数 0(b|B)[0-1_]* */
    NUMBER_BIN(null, CONSTANT),

    /** 十进制小数 [0-9_]*\.[0-9_]+ */
    DECIMAL(null, CONSTANT),

    /** 字符串 \"[.\n]*\" */
    STRING(null, CONSTANT),

    /** 字符 \"[.\n]*\" */
    CHAR(null, CONSTANT),

    /** 行注释 // */ COMMENT_LINE (null, COMMENT),
    /** 块注释 /* */ COMMENT_BLOCK(null, COMMENT),

    /** 换行 */ CR    ("<CR>",     MARK),
    /** 空格 */ BLANK ("< >",      MARK),
    /** 制表 */ TAB   ("<TAB>",    MARK),
    /** 空白 */ SPACES("<SPACES>", MARK),
    /** 结束 */ EOF   ("<EOF>",    MARK);

    public final String value;
    public final KindType[] types;

    Kind(String value, KindType... types) {
        this.value = value;
        this.types = types;
        if (0 == types.length) {
            PanicUtil.panic("can not be empty.");
        }
    }

    @Override
    public String toString() {
        return this.name() + " " + value + " " + Stream.of(types).map(Enum::name).collect(Collectors.joining(" "));
    }

}
