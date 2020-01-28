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
public class MergeOperatorLeft extends Merge {
    public static final Set<Kind> OPERATOR_LEFT   = new HashSet<>(getKinds(KindType.LEFT));
    static {
        OPERATOR_LEFT.remove(Kind.DOT);
        OPERATOR_LEFT.remove(Kind.COLON);
        OPERATOR_LEFT.remove(Kind.AT);
        OPERATOR_LEFT.remove(Kind.HASH);
    }
    public MergeOperatorLeft(Token token) {
        super(token);
    }
}
