package canoe.lexis;

/**
 * @author dawn
 */
public enum Kind {

    // ================ 关键字 ================

    PACKAGE("package"),
    IMPORT("import"),
    AS("as"),

    // 可见性
    PUBLIC("public"),
    PROTECTED("protected"),
    // PACKAGE,
    PRIVATE("private"),

    COLON_PUBLIC(":public"),
    COLON_PROTECTED(":protected"),
    COLON_PACKAGE(":package"),
    COLON_PRIVATE(":private"),

    NATIVE("native"),
    COLON_NATIVE(":native"),

    GOTO("goto"),
    COLON_GOTO(":goto"),

    CANOE("canoe"),
    COLON_CANOE(":canoe"),
    // TODO 多线程怎么交互
    // TODO 枚举怎么实现

    THIS("this"),

    RETURN("return"),

    // 流程控制
    IF("if"),
    ELSE("else"),
    ELSE_IF("else if"),
    MATCH("match"),
    IN("in"),
    WITH("with"),
    LOOP("loop"),
    BREAK("break"),
    CONTINUE("continue"),
    FOR("for"),

    // 布尔常量值
    TRUE("true"),
    FALSE("false"),

    // 基本类型关键字
    INT8("int8"),
    INT16("int16"),
    INT32("int32"),
    INT64("int64"),
    FLOAT32("float32"),
    FLOAT64("float64"),


    // 符号关键字

    // 布尔运算
    LOGICAL_AND("&&"),
    LOGICAL_OR("||"),
    LOGICAL_NOT("!"),

    EQ("="),
    NE("!="),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),

    // ================ 自定义 ================

    /** 标识符 [A-Za-z_][A-Za-z0-9_]* */ ID(""),

    // 数字
    /** 十六进制数 0(x|X)[0-9a-fA-F_]* */ NUMBER_HEXADECIMAL(""),
    /** 十进制数 [1-9][0-9_]* */ NUMBER_DECIMAL(""),
    /** 八进制数 0[1-7][0-7_]* */ NUMBER_OCTAL(""),
    /** 二进制数 0(b|B)[0-1_]* */ NUMBER_BINARY(""),

    /** 十进制小数 [0-9_]*\.[0-9_]+((e|E)(-|\+)?[1-9][0-9]*)? */ REAL_DECIMAL(""),

    /** 字符串 \"[.\n]*\" */ STRING(""),

    /** 注释 // */    NOTE_LINE(""),
    /** 注释 /* */    NOTE_RANGE(""),

    // ================ 符号 ================
    DOT("."),
    CR("\n"),
    ASSIGN_IMMUTABLE("=>"),
    ASSIGN_MUTABLE("->"),

    LB("{"),
    RB("}"),
    LS("["),
    RS("]"),
    LR("("),
    RR(")"),

    COLON(":"),
    COMMA(","),
    SEMI(";"),
    AT("@"),

    // ================ 运算符 ================
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("%"),

    // ================ 二进制运算符 ================
    BIT_AND("&"),
    BIT_OR("|"),
    BIT_XOR("^"),
    BIT_NOT("!"),
    BIT_MOVE_LEFT("<<"),
    BIT_MOVE_RIGHT(">>");
    // TODO 还有左移右移符号


    private String key;

    Kind(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
