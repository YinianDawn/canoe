package canoe.parser.channel.impoerts;

import canoe.ast.imports.ImportStatement;
import canoe.ast.imports.ImportStatementMany;
import canoe.ast.imports.ImportStatementSingle;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.channel.Channel;

import java.util.ArrayList;
import java.util.List;

import static canoe.lexer.Kind.*;
import static canoe.lexer.KindSet.SINGLE_KEY_WORDS;

/**
 * @author dawn
 */
public class ImportChannel extends Channel<ImportStatement> {

    private boolean many = false;

    private ImportChannel(Channel channel, Kind... end) {
        super(channel, end);
        if (glance().not(Kind.IMPORT)) {
            panic("must be import.", glance());
        }
        init();
    }

    @Override
    protected void digest() {
        String status = status();

        if (1 < channelSize()) {
            Token last = getLastToken();
            if (null != last) {
                if (contains(last, SINGLE_KEY_WORDS)) {
                    if (last.is(AS)) { acceptSpaces(); } accept(DOT).refuseAll(); return;
                }
                if (last.is(DOT) || ends(status, "DOT CR", "DOT SPACES")) {
                    if (last.not(DOT)) { removeLast(); }
                    accept(ID).acceptKeyWords().acceptSpaces().accept(MUL).acceptCR().refuseAll(); return;
                }
                if (status.endsWith("AS ID")
                        || (status.endsWith("AS ID SPACES") && null != removeLast())
                        || status.endsWith("DOT MUL")
                        || (status.endsWith("DOT MUL SPACES") && null != removeLast())) {
                    acceptSpaces().acceptCR().refuseAll();
                    if (!many) { match(CR, this::singleFull); }
                    return;
                }
                if (last.is(ID) || ends(status, "ID SPACES")) {
                    if (last.is(ID)) { accept(DOT); } else { accept(AS); removeLast(); }
                    acceptSpaces().acceptCR().refuseAll();
                    if (many) { accept(RR); } else { match(CR, this::singleFull); }
                    return;
                }
                if (status.endsWith("AS SPACES")) { removeLast(); accept(ID).refuseAll(); return; }
                if (last.is(CR)) {
                    if (ends(status,"ID CR", "DOT MUL CR")) { accept(RR); } else { removeLast(); acceptCR(); }
                    accept(Kind.ID).acceptKeyWords().acceptSpaces().refuseAll(); return;
                }
                if (last.is(RR)) { this.manyFull(); refuseAll(); return; }
            }
        }

        switch (status) {
            case "IMPORT": acceptSpaces().accept(Kind.LR).refuseAll(); break;
            case "IMPORT SPACES": removeLast();
                accept(ID).acceptKeyWords().accept(Kind.LR).refuseAll(); break;
            case "IMPORT LR": many = true;
                accept(ID).acceptKeyWords().acceptSpaces().acceptCR().refuseAll(); break;
            default: panic("wrong import statement.");
        }
    }

    private void manyFull() {
        Token importToken = (Token) removeFirst();
        Token lr = (Token) removeFirst();
        List<ImportStatementSingle> importStatementSingles = new ArrayList<>();

        List<Token> importNames = new ArrayList<>();
        Token as = null;
        Token id = null;

        Token rr = null;
        while (isChannelFull()) {
            Token next = (Token) removeFirst();
            switch (next.kind) {
                case RR: rr = next; break;
                case CR:
                    checkKeyWord(importNames);
                    importStatementSingles.add(new ImportStatementSingle(importToken, importNames, as, id));
                    importNames = new ArrayList<>();
                    as = null;
                    id = null;
                    break;
                case AS:
                    if (0 < importNames.size() && !importNames.get(importNames.size() - 1).next(next)) {
                        as = next;
                        break;
                    }
                default:
                    if (null != as) {
                        id = next;
                    } else {
                        importNames.add(next);
                    }
            }
        }
        data = new ImportStatementMany(importToken, lr, importStatementSingles, rr);
    }

    private boolean singleFull() {
        Token importToken = (Token) removeFirst();
        List<Token> names = new ArrayList<>(channelSize());
        Token as = null;
        Token id = null;

        while (isChannelFull()) {
            Token next = (Token) removeFirst();
            if (next.is(AS) && channelSize(1)) {
                as = next;
                id = (Token) removeFirst();
                continue;
            }
            names.add(next);
        }
        checkKeyWord(names);
        data = new ImportStatementSingle(importToken, names, as, id);
        return true;
    }

    private void checkKeyWord(List<Token> names) {
        Token last = names.get(names.size() - 1);
        if (SINGLE_KEY_WORDS.contains(last.kind)) {
            panic("can not be key word.", last);
        }
        if (last.not(ID) && last.not(MUL)) {
            panic("can not be.", last);
        }
    }

    static ImportStatement produce(Channel channel, Kind... end) {
        return new ImportChannel(channel, end).produce();
    }

}
