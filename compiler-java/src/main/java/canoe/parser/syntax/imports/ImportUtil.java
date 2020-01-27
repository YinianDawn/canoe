package canoe.parser.syntax.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;

import java.util.List;

import static canoe.lexer.KindSet.OPERATOR_OVERLOAD;
import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
class ImportUtil {

    static void checkAs(Token as, Token id, String file) {
        if (null != as) {
            if (null == id) {
                panic("must has id", as, file);
            } else if (id.not(Kind.ID, Kind.DOT, Kind.UL)) {
                panic("must be id or . or _", id, file);
            }
        }
    }

    static void checkRound(Token lr, Token rr, Token symbol, List<ImportCommaAs> others, String file) {
        if (null != lr && null == rr) {
            panic("must has )", symbol, file);
        }
        if (null == lr && !others.isEmpty()) {
            panic("must has (", symbol, file);
        }
    }

    static void checkInfo(List<Token> info, String file) {
        if (!info.isEmpty()) {
            Token id = info.get(0);
            if (id.not(Kind.ID)) {
                panic("must be id", id, file);
            }
            Token last = info.get(info.size() - 1);
            if (last.not(Kind.ID, Kind.DOT) && last.not(OPERATOR_OVERLOAD)) {
                panic("must be id or . or overload operator", last, file);
            }
            boolean dot = true;
            for (int i = 1; i < info.size() - 1; i++) {
                Token token = info.get(i);
                if (dot) {
                    if (token.not(Kind.DOT)) {
                        panic("must be .", token, file);
                    }
                    dot = false;
                } else {
                    if (token.not(Kind.ID)) {
                        panic("must be id", token, file);
                    }
                    dot = true;
                }
            }
        }
    }

}
