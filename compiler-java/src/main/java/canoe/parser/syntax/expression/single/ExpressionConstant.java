package canoe.parser.syntax.expression.single;

import canoe.lexer.Kind;
import canoe.lexer.KindType;
import canoe.lexer.Token;
import canoe.parser.syntax.expression.Expression;

import java.util.HashSet;
import java.util.Set;

import static canoe.lexer.KindSet.getKinds;

/**
 * @author dawn
 */
public class ExpressionConstant implements Expression {

    public static final Set<Kind> CONSTANT = new HashSet<>(getKinds(KindType.CONSTANT));

    private final Token constant;

    public ExpressionConstant(Token constant) {
        this.constant = constant;
    }

    @Override
    public Token first() {
        return constant;
    }

    @Override
    public Token last() {
        return constant;
    }
}
