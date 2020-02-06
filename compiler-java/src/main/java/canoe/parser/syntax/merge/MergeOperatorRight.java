package canoe.parser.syntax.merge;

import canoe.lexer.Kind;
import canoe.lexer.KindType;
import canoe.lexer.Token;

import java.util.HashSet;
import java.util.Set;

import static canoe.lexer.KindSet.getKinds;

/**
 * @author dawn
 */
public class MergeOperatorRight extends Merge {
    public static final Set<Kind> OPERATOR_RIGHT  = new HashSet<>(getKinds(KindType.RIGHT));
    static {
        OPERATOR_RIGHT.remove(Kind.DOT_DOT_DOT);
        OPERATOR_RIGHT.remove(Kind.AT);
        OPERATOR_RIGHT.remove(Kind.HASH);
        OPERATOR_RIGHT.remove(Kind.ANTI);
        OPERATOR_RIGHT.remove(Kind.DOLLAR);
        OPERATOR_RIGHT.remove(Kind.BACK);
    }
    public MergeOperatorRight(Token token) {
        super(token);
    }
}
