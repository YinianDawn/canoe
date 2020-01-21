package canoe.lexer;

/**
 * @author dawn
 */
public enum Kind {

    // ================ 关键字 ================

    /** 包声明 */
    PACKAGE("package"),

    /** 类型或函数导入 */
    IMPORT("import"),
    /** 类型或函数导入重命名 */
    AS("as"),

    /** 可见性 */
    OPEN("open"),

    /** 程序原生函数 */
    NATIVE("native"),

    /** goto标签或者跳转至goto标签语句 */
    GOTO("goto"),

    /** 枚举类型 */
    ENUM("enum"),

    /** 新线程 TODO 多线程怎么交互？ */
    CANOE("canoe"),

    /** 返回语句关键词 */
    RETURN("return"),

    // ================ 流程控制语句 ================

    /** if语句 */
    IF("if"),
    /** else语句 */
    ELSE("else"),
    /** else if语句 */
    ELSE_IF("else if"),

    /** match语句 */
    MATCH("match"),
    /** 若是标签 表明本match默认连续执行 */
    WITH("with"),
    /** 若是标签 表明本match默认不连续执行 与with相反 并且without是默认属性 */
    WITHOUT("without"),
    /** in 类似二元操作符 表是前面是否属于后面的 */
    IN("in"),

    /** loop循环语句 loop 后面也可以有语句 第一次时执行一次 */
    LOOP("loop"),
    /** 跳过本层循环 或 跳过指定标签循环 */
    BREAK("break"),
    /** 继续本层循环 或 继续指定标签循环 */
    CONTINUE("continue"),
    /** for循环语句  for ... in ... for语句是遍历的意思  for ;; 语句还是要有 确实好用的 */
    FOR("for"),

    // ================ 其他关键字 ================

    /** 布尔常量值 true */
    TRUE("true"),
    /** 布尔常量值 false */
    FALSE("false"),

    /** 基本类型 */
    INT8("int8"),
    INT16("int16"),
    INT32("int32"),
    INT64("int64"),
    FLOAT32("float32"),
    FLOAT64("float64"),

    // ================ 符号 ================

    // 逻辑运算符
    /** 逻辑与 */
    LOGICAL_AND("&&"),
    /** 逻辑或 */
    LOGICAL_OR("||"),
    /** 逻辑非 与位取反相同保留位取反操作符 */
//    LOGICAL_NOT("!"),

    // 关系运算符
    /** 是否相等 */
    EQ("=="),
    /** 是否不等 */
    NE("!="),
    /** 是否大于 */
    GT(">"),
    /** 是否大于等于 */
    GE(">="),
    /** 是否小于 */
    LT("<"),
    /** 是否小于等于 */
    LE("<="),

    /** lambda表达式 */
    LAMBDA("->"),

    /** 赋值 不改变约束 */
    ASSIGN("="),
    /** 强制赋值 可以改变约束 */
    ASSIGN_FORCE(":="),

    /** 包名可能有 指明属性(当前对象的) */
    DOT("."),
    /** 指明属性(上个对象的) */
    DOT_DOT(".."),
    /** 函数输入可变长度 必须最后一个参数才允许 */
    DOT_DOT_DOT("..."),

    // 范围界限符号 成对出现 <> 是泛型界限，已经大于小于符号了
    LB("{"),
    RB("}"),
    LS("["),
    RS("]"),
    LR("("),
    RR(")"),

    /** 描述属性 match子句  */
    COLON(":"),

    /** 多个函数参数分隔 数组元素分隔 */
    COMMA(","),

    /** 语句之间分隔 换行符也是语句分隔作用 */
    SEMI(";"),
    /** 暂时无用 */
    AT("@"),
    /** 暂时无用 也许将来注解使用 */
    HASH("#"),

    // 数学运算符
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("%"),

    // 位运算符
    BIT_AND("&"),
    BIT_OR("|"),
    BIT_XOR("^"),
    BIT_NOT("!"),
    BIT_LEFT("<<"),
    BIT_RIGHT(">>"),

    // 自增自减运算
    ADD_ADD("++"),
    SUB_SUB("--"),

    // 运算并赋值
    ADD_ASSIGN("+="),
    SUB_ASSIGN("-="),
    MUL_ASSIGN("*="),
    DIV_ASSIGN("/="),
    MOD_ASSIGN("%="),

    BIT_AND_ASSIGN("&="),
    BIT_OR_ASSIGN("|="),
    BIT_XOR_ASSIGN("^="),
    BIT_LEFT_ASSIGN("<<="),
    BIT_RIGHT_ASSIGN(">>="),

    // ================ 无法枚举的符号 ================

    /** 标识符 [A-Za-z_][A-Za-z0-9_]* */
    ID(null),

    // 数字
    /** 十六进制数 0(x|X)[0-9a-fA-F_]* */
    NUMBER_HEX(null),
    /** 十进制数 [1-9][0-9_]* */
    NUMBER_DEC(null),
    /** 八进制数 0[1-7][0-7_]* */
    NUMBER_OCT("NUMBER_OCT"),
    /** 二进制数 0(b|B)[0-1_]* */
    NUMBER_BIN(null),

    /** 十进制小数 [0-9_]*\.[0-9_]+ */
    DECIMAL(null),

    /** 字符串 \"[.\n]*\" */
    STRING(null),

    /** 行注释 // */
    COMMENT_LINE(null),
    /** 块注释 /* */
    COMMENT_BLOCK(null),

    /** 换行符 */
    CR("<\\n>"),
    /** 空格 */
    SPACES("<SPACES>"),
    /** 文件结束 */
    EOF("<EOF>");

    private String sign;

    Kind(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

}
