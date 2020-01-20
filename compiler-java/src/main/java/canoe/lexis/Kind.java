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
    OPEN("open"),

    COLON_OPEN(":open"),

    NATIVE("native"),
    COLON_NATIVE(":native"),

    GOTO("goto"),
    COLON_GOTO(":goto"),

    ENUM("enum"),
    COLON_ENUM(":enum"),

    CANOE("canoe"),
    // TODO 多线程怎么交互

    RETURN("return"),

    // 流程控制
    IF("if"),
    ELSE("else"),
    ELSE_IF("else if"),
    MATCH("match"),
    IN("in"),
    WITH("with"),
    COLON_WITH(":with"),
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
//    LOGICAL_NOT("!"),

    EQ("=="),
    NE("!="),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),

    // lambda
    LAMBDA("->"),

    // 赋值
    ASSIGN("="),
    ASSIGN_FORCE(":="),

    // 符号
    DOT("."),
    DOT_DOT(".."),
    DOT_DOT_DOT("..."),
    CR("\n"),

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
    POUND("#"),

    // 数学运算符
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    MOD("%"),

    // 二进制运算
    BIT_AND("&"),
    BIT_OR("|"),
    BIT_XOR("^"),
    BIT_NOT("!"),
    BIT_MOVE_LEFT("<<"),
    BIT_MOVE_RIGHT(">>"),

    ADD_ADD("++"),
    SUB_SUB("--"),

    ADD_ASSIGN("+="),
    SUB_ASSIGN("-="),
    MUL_ASSIGN("*="),
    DIV_ASSIGN("/="),
    MOD_ASSIGN("%="),

    BIT_AND_ASSIGN("&="),
    BIT_OR_ASSIGN("|="),
    BIT_XOR_ASSIGN("^="),
    BIT_MOVE_LEFT_ASSIGN("<<="),
    BIT_MOVE_RIGHT_ASSIGN(">>="),

    // ================ 自定义 ================

    /** 标识符 [A-Za-z_][A-Za-z0-9_]* */ ID(""),

    // 数字
    /** 十六进制数 0(x|X)[0-9a-fA-F_]* */ NUMBER_HEXADECIMAL(""),
    /** 十进制数 [1-9][0-9_]* */ NUMBER_DECIMAL(""),
    /** 八进制数 0[1-7][0-7_]* */ NUMBER_OCTAL(""),
    /** 二进制数 0(b|B)[0-1_]* */ NUMBER_BINARY(""),

    /** 十进制小数 [0-9_]*\.[0-9_]+ */ REAL_DECIMAL(""),

    /** 字符串 \"[.\n]*\" */ STRING(""),

    /** 注释 // */    COMMENT_LINE(""),
    /** 注释 /* */    COMMENT_BLOCK(""),

    SPACES("<SPACES>"),
    EOF("<EOF>");

    private String key;

    Kind(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
