package canoe.lexer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
    static { SINGLE_KEY_WORDS.remove(Kind.ELSE_IF); }

    public static final Set<Kind> CONSTANT = new HashSet<>(getKinds(KindType.CONSTANT));

    public static final Set<Kind> OPERATOR_OVERLOAD = new HashSet<>(getKinds(KindType.OVERLOAD));

    public static final Set<Kind> OPERATOR_RELATION = new HashSet<>(getKinds(KindType.OPERATOR_RELATION));
    public static final Set<Kind> OPERATOR_LOGICAL  = new HashSet<>(getKinds(KindType.OPERATOR_LOGICAL));
    public static final Set<Kind> OPERATOR_MATH     = new HashSet<>(getKinds(KindType.OPERATOR_MATH));
    public static final Set<Kind> OPERATOR_BIT      = new HashSet<>(getKinds(KindType.OPERATOR_BIT));
    public static final Set<Kind> OPERATOR_SELF     = new HashSet<>(getKinds(KindType.OPERATOR_SELF));

    public static final Set<Kind> OPERATOR_ASSIGN   = new HashSet<>(getKinds(KindType.OPERATOR_ASSIGN));

    public static final Set<Kind> OPERATOR_MIDDLE = new HashSet<>(getKinds(KindType.MIDDLE));
    public static final Set<Kind> OPERATOR_LEFT   = new HashSet<>(getKinds(KindType.LEFT));
    public static final Set<Kind> OPERATOR_RIGHT  = new HashSet<>(getKinds(KindType.RIGHT));

}
