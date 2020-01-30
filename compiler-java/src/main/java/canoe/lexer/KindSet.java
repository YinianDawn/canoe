package canoe.lexer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author dawn
 */
public class KindSet {

    public static List<Kind> getKinds(KindType type) {
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

    public static final Set<Kind> KEY_WORDS = new HashSet<>(getKinds(KindType.KEY_WORD));

//    public static final Set<Kind> OPERATOR_RELATION = new HashSet<>(getKinds(KindType.OPERATOR_RELATION));
//    public static final Set<Kind> OPERATOR_LOGIC  = new HashSet<>(getKinds(KindType.OPERATOR_LOGIC));
//    public static final Set<Kind> OPERATOR_MATH     = new HashSet<>(getKinds(KindType.OPERATOR_MATH));
//    public static final Set<Kind> OPERATOR_BIT      = new HashSet<>(getKinds(KindType.OPERATOR_BIT));
//    public static final Set<Kind> OPERATOR_SELF     = new HashSet<>(getKinds(KindType.OPERATOR_SELF));

}
