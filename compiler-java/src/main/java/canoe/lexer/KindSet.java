package canoe.lexer;

import java.util.HashSet;
import java.util.Set;

/**
 * @author dawn
 */
public class KindSet {

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

}
