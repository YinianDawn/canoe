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
import static canoe.lexer.KindSet.COMMON_KEY_WORDS;
import static canoe.lexer.KindSet.contains;

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
    protected void init() {
        while (hunger()) { eat(); }
    }

    @Override
    protected void digest() {
        String status = status();

        if (1 < channelSize()) {
            Token last = getLastToken();
            if (null != last) {
                if (many) {
                    if (contains(COMMON_KEY_WORDS, last)) {
                        if (last.is(AS)) { accept(DOT).acceptSpaces().refuseAll(); return; }
                        else { accept(DOT).refuseAll(); return; }
                    }
                    if (last.is(CR)) {
                        if (status.endsWith("ID CR")) {
                            accept(Kind.ID).acceptKeyWords().acceptSpaces().accept(RR).refuseAll(); return;
                        } else {
                            removeLast();
                            accept(Kind.ID).acceptKeyWords().acceptSpaces().acceptCR().refuseAll();
                            return;
                        }
                    }
                    if (last.is(DOT)) {
                        accept(ID).acceptKeyWords().acceptSpaces().acceptCR().refuseAll(); return;
                    }
                    if (status.endsWith("DOT CR") || status.endsWith("DOT SPACES")) {
                        removeLast();
                        accept(ID).acceptKeyWords().acceptSpaces().acceptCR().refuseAll(); return;
                    }
                    if (last.is(ID)) {
                        accept(DOT).acceptSpaces().acceptCR().accept(RR).refuseAll(); return;
                    }
                    if (status.endsWith("ID SPACES")) {
                        removeLast();
                        accept(AS).acceptSpaces().acceptCR().accept(RR).refuseAll(); return;
                    }
                    if (status.endsWith("AS SPACES")) {
                        removeLast();
                        accept(ID).refuseAll(); return;
                    }
                    if (last.is(RR)) {
                        this.manyFull();
                        refuseAll();
                        return;
                    }
                } else {
                    if (contains(COMMON_KEY_WORDS, last)) {
                        if (last.is(AS)) { accept(DOT).acceptSpaces().refuseAll(); return; }
                        else { accept(DOT).refuseAll(); return; }
                    }
                    if (last.is(DOT)) {
                        accept(ID).acceptKeyWords().acceptSpaces().acceptCR().refuseAll(); return;
                    }
                    if (status.endsWith("DOT CR") || status.endsWith("DOT SPACES")) {
                        removeLast();
                        accept(ID).acceptKeyWords().acceptSpaces().acceptCR().refuseAll(); return;
                    }
                    if (status.endsWith("AS ID")) {
                        acceptSpaces().acceptCR().refuseAll().match(CR, this::singleFull); return;
                    }
                    if (status.endsWith("AS ID SPACES")) {
                        removeLast();
                        acceptSpaces().acceptCR().refuseAll().match(CR, this::singleFull); return;
                    }
                    if (last.is(ID)) {
                        accept(DOT).acceptSpaces().acceptCR().refuseAll().match(CR, this::singleFull); return;
                    }
                    if (status.endsWith("ID SPACES")) {
                        removeLast();
                        accept(AS).acceptSpaces().acceptCR().refuseAll().match(CR, this::singleFull); return;
                    }
                    if (status.endsWith("AS SPACES")) {
                        removeLast();
                        accept(ID).refuseAll(); return;
                    }
                }
            }
        }

        switch (status) {
            case "IMPORT":
                acceptSpaces().accept(Kind.LR).refuseAll(); break;
            case "IMPORT SPACES":
                removeLast();
                accept(Kind.ID).acceptKeyWords().accept(Kind.LR).refuseAll(); break;
            case "IMPORT LR":
                many = true;
                accept(Kind.ID).acceptKeyWords().acceptSpaces().acceptCR().refuseAll();
                break;
            default: panic("wrong import statement.");
        }
    }

    private boolean manyFull() {
        Token importToken = (Token) removeFirst();
        Token lr = (Token) removeFirst();
        List<ImportStatementSingle> importStatementSingles = new ArrayList<>();

        List<Token> importNames = new ArrayList<>();
        Token as = null;
        Token id = null;

        Token rr = null;
        while (channelFull()) {
            Token next = (Token) removeFirst();
            switch (next.kind) {
                case RR: rr = next; break;
                case CR:
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
        return true;
    }

    private boolean singleFull() {
        Token importToken = (Token) removeFirst();
        List<Token> names = new ArrayList<>(channelSize());
        Token as = null;
        Token id = null;

        while (channelFull()) {
            Token next = (Token) removeFirst();
            if (next.is(AS) && 1 == channelSize()) {
                as = next;
                id = (Token) removeFirst();
                continue;
            }
            names.add(next);
        }
        data = new ImportStatementSingle(importToken, names, as, id);
        return true;
    }

    //
//    private boolean done() {
//        return glance().not(Kind.IMPORT);
//    }
//
//
//    public ImportStatements get() {
//        while (!done()) { parse(); }
//        return new ImportStatements(importStatements);
//    }
//
//
//
//    private void parse() {
//        Token next = glance();
//        if (!COMMON_KEY_WORDS.contains(next.kind)){
//
//            if (eatSpaceOrCR(next)) {
//                next(); parse(); return;
//            }
//
//            switch (next.kind) {
//                case DOT: case ID: break;
//                case LR: case RR: break;
//                default: panic("can not be: " + next, next);
//            }
//        }
//        channel.addLast(next());
//        reduce();
//    }
//
//    private void reduce() {
//        String status = status();
//
//        if (1 < channel.size()) {
//            Object o = channel.getLast();
//            if (o instanceof Token) {
//                Token token = (Token) o;
//                if (COMMON_KEY_WORDS.contains(token.kind)) {
//                    if (glanceSkipSpace().isDot()) {
//                        status = status.substring(0, status.lastIndexOf(" ") + 1) + "ID";
//                    } else if (token.not(Kind.AS)) {
//                        panic("can not end with key word.");
//                    }
//                }
//            }
//        }
//
//        if (status.endsWith("IMPORT NameList") && glanceSkipSpace().isCR()) {
//            List<Token> names = ((NameList)channel.removeLast()).names;
//            Token importToken = (Token) channel.removeLast();
//            importStatements.add(new ImportStatementSingle(importToken, names, null, null));
//            removeSpaceOrCR(); return;
//        }
//
//        if (status.endsWith("IMPORT NameList AS ID")) {
//            if (!glanceSkipSpace().isCR()) {
//                panic("must end with CR(\\ n).", glanceSkipSpace());
//            }
//            Token id = (Token) channel.removeLast();
//            Token as = (Token) channel.removeLast();
//            List<Token> names = ((NameList)channel.removeLast()).names;
//            Token importToken = (Token) channel.removeLast();
//            importStatements.add(new ImportStatementSingle(importToken, names, as, id));
//            removeSpaceOrCR(); return;
//        }
//
//        if (status.endsWith("IMPORT LR NameList") && glanceSkipSpace().isCR()) {
//            List<Token> names = ((NameList)channel.removeLast()).names;
//            Token importToken = (Token) channel.getFirst();
//            importStatementSingles.add(new ImportStatementSingle(importToken, names, null, null));
//            removeSpaceOrCR(); parse(); return;
//        }
//
//        if (status.endsWith("IMPORT LR NameList AS ID")) {
//            if (!glanceSkipSpace().isCR()) {
//                panic("must end with CR(\\ n).", glanceSkipSpace());
//            }
//            Token id = (Token) channel.removeLast();
//            Token as = (Token) channel.removeLast();
//            Token importToken = (Token) channel.getFirst();
//            List<Token> names = ((NameList)channel.removeLast()).names;
//            importStatementSingles.add(new ImportStatementSingle(importToken, names, as, id));
//            removeSpaceOrCR(); parse(); return;
//        }
//
//        if (status.endsWith("IMPORT LR RR")) {
//            if (!glanceSkipSpace().isCR()) {
//                panic("must end with CR(\\ n).", glanceSkipSpace());
//            }
//            Token rr = (Token) channel.removeLast();
//            Token lr = (Token) channel.removeLast();
//            Token importToken = (Token) channel.removeLast();
//            importStatements.add(new ImportStatementMany(importToken, lr, new ArrayList<>(importStatementSingles), rr));
//            importStatementSingles.clear();
//            removeSpaceOrCR();
//            return;
//        }
//
//        switch (status) {
//            case "IMPORT": accept(true, false); parse(); break;
//
//            case "IMPORT LR ID":
//            case "IMPORT ID":
//                channel.addLast(new NameList((Token) channel.removeLast()));
//                if (glanceSkipSpace().isCR()) {
//                    reduce();
//                } else {
//                    accept(glanceSkipSpace().is(Kind.AS), false);
//                    parse();
//                } break;
//            case "IMPORT LR NameList DOT":
//            case "IMPORT NameList DOT":
//                mergeName(status);
//                accept(true, true); parse(); break;
//            case "IMPORT LR NameList ID":
//            case "IMPORT NameList ID":
//                mergeName(status);
//                if (glanceSkipSpace().isCR()) {
//                    reduce();
//                } else {
//                    accept(glanceSkipSpace().is(Kind.AS), false);
//                    parse();
//                } break;
//
//            case "IMPORT LR NameList AS":
//            case "IMPORT NameList AS":
//                if (glanceSkipSpaceOrCR().not(Kind.ID)) {
//                    panic("word after as must be ID kind.", glanceSkipSpaceOrCR());
//                }
//                accept(true, true); parse(); break;
//
//            case "IMPORT LR": accept(true, true); parse(); break;
//
//            default: panic("wrong import statement.", current());
//        }
//    }
//
//    private void mergeName(String status) {
//        if (status.endsWith("NameList DOT") || status.endsWith("NameList ID")) {
//            Token token = (Token) channel.removeLast();
//            NameList nameList = (NameList) channel.removeLast();
//            nameList.names.add(token);
//            channel.addLast(nameList);
//        } else {
//            panic("can not merge names by : " + status);
//        }
//    }
//
//    private static class NameList {
//        List<Token> names = new LinkedList<>();
//        NameList(Token id) {
//            this.names.add(id);
//        }
//    }

    static ImportStatement produce(Channel channel, Kind... end) {
        return new ImportChannel(channel, end).produce();
    }

}
