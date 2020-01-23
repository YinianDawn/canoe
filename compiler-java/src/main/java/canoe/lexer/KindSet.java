package canoe.lexer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static canoe.lexer.KindType.*;

/**
 * @author dawn
 */
public class KindSet {

    private static List<Kind> getKinds(KindType type) {
        List<Kind> kinds = new LinkedList<>();
        for (Kind kind : Kind.values()) {
            for (KindType kt : kind.types) {
                if (kt == type) {
                    kinds.add(kind);
                    break;
                }
            }
        }
        return kinds;
    }

    public static final Set<Kind> SINGLE_KEY_WORDS = new HashSet<>(getKinds(KindType.KEY_WORD));

    static {
        // else if 有空格
        SINGLE_KEY_WORDS.remove(Kind.ELSE_IF);
    }

    public static final Set<Kind> CONSTANT = new HashSet<>(getKinds(KindType.CONSTANT));


    public static final Set<Kind> MIDDLE_OPERATOR = new HashSet<>(getKinds(KindType.MIDDLE));

    static {
        // 赋值运算符不算中间
        MIDDLE_OPERATOR.removeAll(getKinds(SIGN_ASSIGN));
    }

    public static final Set<Kind> LEFT_OPERATOR = new HashSet<>(getKinds(FIRST));

    static {
        // 有些符号虽然要放在前面，但不是操作符
        LEFT_OPERATOR.removeAll(getKinds(SIGN_MARK));
    }

    public static final Set<Kind> RIGHT_OPERATOR = new HashSet<>(getKinds(FOLLOW));

    static {
        // 有些符号虽然要放在前面，但不是操作符
        RIGHT_OPERATOR.removeAll(getKinds(SIGN_MARK));
    }

    public static final Set<Kind> ASSIGN_OPERATOR = new HashSet<>(getKinds(SIGN_ASSIGN));


    public static final Set<Kind> RELATION_OPERATOR = new HashSet<>(getKinds(SIGN_RELATION));

}
