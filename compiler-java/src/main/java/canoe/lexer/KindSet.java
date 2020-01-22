package canoe.lexer;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dawn
 */
public class KindSet {

    public static boolean contains(Set<Kind> set, Token token) {
        return set.contains(token.kind);
    }

    public static boolean contains(Set<Kind> set, Kind kind) {
        return set.contains(kind);
    }

    public static final Set<Kind> COMMON_KEY_WORDS = new HashSet<>();

    static {
        COMMON_KEY_WORDS.add(Kind.PACKAGE);
        COMMON_KEY_WORDS.add(Kind.IMPORT);
        COMMON_KEY_WORDS.add(Kind.AS);
        COMMON_KEY_WORDS.add(Kind.OPEN);
        COMMON_KEY_WORDS.add(Kind.NATIVE);
        COMMON_KEY_WORDS.add(Kind.GOTO);
        COMMON_KEY_WORDS.add(Kind.ENUM);
        COMMON_KEY_WORDS.add(Kind.CANOE);
        COMMON_KEY_WORDS.add(Kind.RETURN);
        COMMON_KEY_WORDS.add(Kind.IF);
        COMMON_KEY_WORDS.add(Kind.ELSE);
        COMMON_KEY_WORDS.add(Kind.MATCH);
        COMMON_KEY_WORDS.add(Kind.WITH);
        COMMON_KEY_WORDS.add(Kind.WITHOUT);
        COMMON_KEY_WORDS.add(Kind.IN);
        COMMON_KEY_WORDS.add(Kind.LOOP);
        COMMON_KEY_WORDS.add(Kind.BREAK);
        COMMON_KEY_WORDS.add(Kind.CONTINUE);
        COMMON_KEY_WORDS.add(Kind.FOR);
        COMMON_KEY_WORDS.add(Kind.TRUE);
        COMMON_KEY_WORDS.add(Kind.FALSE);
        COMMON_KEY_WORDS.add(Kind.INT8);
        COMMON_KEY_WORDS.add(Kind.INT16);
        COMMON_KEY_WORDS.add(Kind.INT32);
        COMMON_KEY_WORDS.add(Kind.INT64);
        COMMON_KEY_WORDS.add(Kind.FLOAT32);
        COMMON_KEY_WORDS.add(Kind.FLOAT64);
    }

    public static final Set<Kind> CONSTANT = new HashSet<>();

    static {
        CONSTANT.add(Kind.NUMBER_HEX);
        CONSTANT.add(Kind.NUMBER_DEC);
        CONSTANT.add(Kind.NUMBER_OCT);
        CONSTANT.add(Kind.NUMBER_BIN);

        CONSTANT.add(Kind.DECIMAL);

        CONSTANT.add(Kind.STRING);

        CONSTANT.add(Kind.TRUE);
        CONSTANT.add(Kind.FALSE);
    }

    public static final Set<Kind> BINARY_OPERATOR = new HashSet<>();

    static {
        BINARY_OPERATOR.add(Kind.LOGICAL_AND);
        BINARY_OPERATOR.add(Kind.LOGICAL_OR);

        BINARY_OPERATOR.add(Kind.EQ);
        BINARY_OPERATOR.add(Kind.NE);
        BINARY_OPERATOR.add(Kind.GT);
        BINARY_OPERATOR.add(Kind.GE);
        BINARY_OPERATOR.add(Kind.LT);
        BINARY_OPERATOR.add(Kind.LE);

        BINARY_OPERATOR.add(Kind.LAMBDA);

        BINARY_OPERATOR.add(Kind.DOT);
        BINARY_OPERATOR.add(Kind.DOT_DOT);

        BINARY_OPERATOR.add(Kind.ADD);
        BINARY_OPERATOR.add(Kind.SUB);
        BINARY_OPERATOR.add(Kind.MUL);
        BINARY_OPERATOR.add(Kind.DIV);
        BINARY_OPERATOR.add(Kind.MOD);

        BINARY_OPERATOR.add(Kind.BIT_AND);
        BINARY_OPERATOR.add(Kind.BIT_OR);
        BINARY_OPERATOR.add(Kind.BIT_XOR);
        BINARY_OPERATOR.add(Kind.BIT_LEFT);
        BINARY_OPERATOR.add(Kind.BIT_RIGHT);

        BINARY_OPERATOR.add(Kind.COMMA);

    }

    public static final Set<Kind> LEFT_OPERATOR = new HashSet<>();

    static {
        LEFT_OPERATOR.add(Kind.BIT_NOT);
        LEFT_OPERATOR.add(Kind.ADD);
        LEFT_OPERATOR.add(Kind.SUB);
    }

    public static final Set<Kind> RIGHT_OPERATOR = new HashSet<>();

    static {
        RIGHT_OPERATOR.add(Kind.ADD_ADD);
        RIGHT_OPERATOR.add(Kind.SUB_SUB);
    }

    public static final Set<Kind> ASSIGN_OPERATOR = new HashSet<>();

    static {
        ASSIGN_OPERATOR.add(Kind.ASSIGN);
        ASSIGN_OPERATOR.add(Kind.ASSIGN_FORCE);

        ASSIGN_OPERATOR.add(Kind.ADD_ASSIGN);
        ASSIGN_OPERATOR.add(Kind.SUB_ASSIGN);
        ASSIGN_OPERATOR.add(Kind.MUL_ASSIGN);
        ASSIGN_OPERATOR.add(Kind.DIV_ASSIGN);
        ASSIGN_OPERATOR.add(Kind.MOD_ASSIGN);

        ASSIGN_OPERATOR.add(Kind.BIT_AND_ASSIGN);
        ASSIGN_OPERATOR.add(Kind.BIT_OR_ASSIGN);
        ASSIGN_OPERATOR.add(Kind.BIT_XOR_ASSIGN);
        ASSIGN_OPERATOR.add(Kind.BIT_LEFT_ASSIGN);
        ASSIGN_OPERATOR.add(Kind.BIT_RIGHT_ASSIGN);
    }


    public static final Set<Kind> RELATIONAL_OPERATOR = new HashSet<>();

    static {
        RELATIONAL_OPERATOR.add(Kind.EQ);
        RELATIONAL_OPERATOR.add(Kind.NE);
        RELATIONAL_OPERATOR.add(Kind.GT);
        RELATIONAL_OPERATOR.add(Kind.GE);
        RELATIONAL_OPERATOR.add(Kind.LT);
        RELATIONAL_OPERATOR.add(Kind.LE);
    }

}
