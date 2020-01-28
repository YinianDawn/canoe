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
public class MergeAssign extends Merge {

    public static final Set<Kind> OPERATOR_ASSIGN   = new HashSet<>(getKinds(KindType.OPERATOR_ASSIGN));

    public MergeAssign(Token token) {
        super(token);
    }
}
