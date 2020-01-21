package canoe.parser.channel;

import canoe.ast.imports.ImportStatement;
import canoe.ast.imports.ImportStatementMany;
import canoe.ast.imports.ImportStatementSingle;
import canoe.ast.imports.ImportStatements;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.TokenStream;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static canoe.lexer.KindSet.COMMON_KEY_WORDS;

/**
 * @author dawn
 */
public class ImportChannel extends Channel {

    private List<ImportStatement> importStatements = new LinkedList<>();

    private List<ImportStatementSingle> importStatementSingles = new LinkedList<>();

    public ImportChannel(String name, TokenStream stream) {
        super(name, stream);
        if (stream.glance().not(Kind.IMPORT)) {
            panic("must be import.", stream.glance());
        }
    }

    public ImportStatements get() {
        while (!done()) { parse(); }
        return new ImportStatements(importStatements);
    }

    private boolean done() {
        return glance().not(Kind.IMPORT);
    }

    private void parse() {
        Token next = glance();
        if (!COMMON_KEY_WORDS.contains(next.kind)){

            if (eatSpaceOrCR(next)) {
                next(); parse(); return;
            }

            switch (next.kind) {
                case DOT: case ID: break;
                case LR: case RR: break;
                default: panic("can not be: " + next, next);
            }
        }
        channel.addLast(next());
        reduce();
    }

    private void reduce() {
        String status = status();

        if (1 < channel.size()) {
            Object o = channel.getLast();
            if (o instanceof Token) {
                Token token = (Token) o;
                if (COMMON_KEY_WORDS.contains(token.kind)) {
                    if (glanceSkipSpace().isDot()) {
                        status = status.substring(0, status.lastIndexOf(" ") + 1) + "ID";
                    } else if (token.not(Kind.AS)) {
                        panic("can not end with key word.");
                    }
                }
            }
        }

        if (status.endsWith("IMPORT NameList") && glanceSkipSpace().isCR()) {
            List<Token> names = ((NameList)channel.removeLast()).names;
            Token importToken = (Token) channel.removeLast();
            importStatements.add(new ImportStatementSingle(importToken, names, null, null));
            removeSpaceOrCR(); return;
        }

        if (status.endsWith("IMPORT NameList AS ID")) {
            if (!glanceSkipSpace().isCR()) {
                panic("must end with CR(\\ n).", glanceSkipSpace());
            }
            Token id = (Token) channel.removeLast();
            Token as = (Token) channel.removeLast();
            List<Token> names = ((NameList)channel.removeLast()).names;
            Token importToken = (Token) channel.removeLast();
            importStatements.add(new ImportStatementSingle(importToken, names, as, id));
            removeSpaceOrCR(); return;
        }

        if (status.endsWith("IMPORT LR NameList") && glanceSkipSpace().isCR()) {
            List<Token> names = ((NameList)channel.removeLast()).names;
            Token importToken = (Token) channel.getFirst();
            importStatementSingles.add(new ImportStatementSingle(importToken, names, null, null));
            removeSpaceOrCR(); parse(); return;
        }

        if (status.endsWith("IMPORT LR NameList AS ID")) {
            if (!glanceSkipSpace().isCR()) {
                panic("must end with CR(\\ n).", glanceSkipSpace());
            }
            Token id = (Token) channel.removeLast();
            Token as = (Token) channel.removeLast();
            Token importToken = (Token) channel.getFirst();
            List<Token> names = ((NameList)channel.removeLast()).names;
            importStatementSingles.add(new ImportStatementSingle(importToken, names, as, id));
            removeSpaceOrCR(); parse(); return;
        }

        if (status.endsWith("IMPORT LR RR")) {
            if (!glanceSkipSpace().isCR()) {
                panic("must end with CR(\\ n).", glanceSkipSpace());
            }
            Token rr = (Token) channel.removeLast();
            Token lr = (Token) channel.removeLast();
            Token importToken = (Token) channel.removeLast();
            importStatements.add(new ImportStatementMany(importToken, lr, new ArrayList<>(importStatementSingles), rr));
            importStatementSingles.clear();
            removeSpaceOrCR();
            return;
        }

        switch (status) {
            case "IMPORT": accept(true, false); parse(); break;

            case "IMPORT LR ID":
            case "IMPORT ID":
                channel.addLast(new NameList((Token) channel.removeLast()));
                if (glanceSkipSpace().isCR()) {
                    reduce();
                } else {
                    accept(glanceSkipSpace().is(Kind.AS), false);
                    parse();
                } break;
            case "IMPORT LR NameList DOT":
            case "IMPORT NameList DOT":
                mergeName(status);
                accept(true, true); parse(); break;
            case "IMPORT LR NameList ID":
            case "IMPORT NameList ID":
                mergeName(status);
                if (glanceSkipSpace().isCR()) {
                    reduce();
                } else {
                    accept(glanceSkipSpace().is(Kind.AS), false);
                    parse();
                } break;

            case "IMPORT LR NameList AS":
            case "IMPORT NameList AS":
                if (glanceSkipSpaceOrCR().not(Kind.ID)) {
                    panic("word after as must be ID kind.", glanceSkipSpaceOrCR());
                }
                accept(true, true); parse(); break;

            case "IMPORT LR": accept(true, true); parse(); break;

            default: panic("wrong import statement.", current());
        }
    }

    private void mergeName(String status) {
        if (status.endsWith("NameList DOT") || status.endsWith("NameList ID")) {
            Token token = (Token) channel.removeLast();
            NameList nameList = (NameList) channel.removeLast();
            nameList.names.add(token);
            channel.addLast(nameList);
        } else {
            panic("can not merge names by : " + status);
        }
    }

    private static class NameList {
        List<Token> names = new LinkedList<>();
        NameList(Token id) {
            this.names.add(id);
        }
    }

}
