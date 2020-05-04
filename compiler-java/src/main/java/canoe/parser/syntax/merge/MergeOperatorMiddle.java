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
public class MergeOperatorMiddle extends Merge {

    public static final Set<Kind> OPERATOR_MIDDLE = new HashSet<>(getKinds(KindType.MIDDLE));

    static {
        OPERATOR_MIDDLE.remove(Kind.DOT);
        OPERATOR_MIDDLE.remove(Kind.POINT);
        OPERATOR_MIDDLE.remove(Kind.IN);
        OPERATOR_MIDDLE.remove(Kind.COMMA);
        OPERATOR_MIDDLE.remove(Kind.SEMI);
        OPERATOR_MIDDLE.remove(Kind.COLON);
        OPERATOR_MIDDLE.remove(Kind.COLON_BLANK);
        OPERATOR_MIDDLE.remove(Kind.BLANK_COLON_BLANK);
        OPERATOR_MIDDLE.remove(Kind.AT);
        OPERATOR_MIDDLE.remove(Kind.HASH);
        OPERATOR_MIDDLE.remove(Kind.ANTI);
        OPERATOR_MIDDLE.remove(Kind.DOLLAR);
        OPERATOR_MIDDLE.remove(Kind.BACK);
    }

    public MergeOperatorMiddle(Token token) {
        super(token);
    }
}
