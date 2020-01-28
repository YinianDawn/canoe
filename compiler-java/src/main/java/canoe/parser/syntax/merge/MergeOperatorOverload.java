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
public class MergeOperatorOverload extends Merge {

    public static final Set<Kind> OPERATOR_OVERLOAD = new HashSet<>(getKinds(KindType.OVERLOAD));

    public MergeOperatorOverload(Token token) {
        super(token);
    }
}
