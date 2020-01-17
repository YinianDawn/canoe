package canoe.compiler;

import java.util.Map;
import java.util.HashMap;

/**
 * @author dawn
 */
public enum CompilerMode {
    /**
     * 检查语法
     */
    CHECK_SYNTAX ("--check-syntax"),
    DUMP_TOKENS ("--dump-tokens"),
    DUMP_AST ("--dump-ast"),
    DUMP_STMT ("--dump-stmt"),
    DUMP_EXPR ("--dump-expr"),
    DUMP_SEMANTIC ("--dump-semantic"),
    DUMP_REFERENCE ("--dump-reference"),
    DUMP_IR ("--dump-ir"),
    DUMP_ASM ("--dump-asm"),
    PRINT_ASM ("--print-asm"),
    COMPILE ("-S"),
    ASSEMBLE ("-c"),
    LINK ("--link");

    private final String option;

    CompilerMode(String option) { this.option = option; }

    private static Map<String, CompilerMode> modes;
    static {
        modes = new HashMap<>();
        for (CompilerMode cm : CompilerMode.values()) {
            modes.put(cm.option, cm);
        }
    }

    public static boolean isModeOption(String opt) {
        return modes.containsKey(opt);
    }

    static public CompilerMode fromOption(String opt) {
        CompilerMode m = modes.get(opt);
        if (m == null) {
            throw new Error("must not happen: unknown mode option: " + opt);
        }
        return m;
    }


    public String toOption() {
        return option;
    }

    boolean requires(CompilerMode m) {
        return ordinal() >= m.ordinal();
    }

}
