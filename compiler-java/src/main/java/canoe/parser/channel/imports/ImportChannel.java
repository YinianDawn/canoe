package canoe.parser.channel.imports;

import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.TokenStream;
import canoe.parser.channel.Channel;
import canoe.parser.syntax.ImportInfo;
import canoe.parser.syntax.imports.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dawn
 */
public class ImportChannel extends Channel<ImportInfo> {

    private List<ImportStatement> importStatements = new ArrayList<>();

    private ImportChannel(TokenStream stream) {
        super(stream, Arrays.stream(Kind.values())
                .filter(k -> k != Kind.IMPORT)
                .collect(Collectors.toList())
                .toArray(new Kind[]{}));
        dropSpacesCR();
        init();
    }

    @Override
    protected boolean eat(Token next) {
        if (next.is(Kind.IMPORT)) {
            // import 开头 尝试解析import语句
            Token symbol = next();
            dropSpaces();
            next = glance();
            switch (next.kind) {
                case UL:
                case MUL:
                case ID:
                case STRING:
                    ImportUnit unit = ImportUnitChannel.make(this, Kind.CR);
                    importStatements.add(new ImportSingle(symbol, unit));
                    break;
                case LR:
                    Token lr = next();
                    List<ImportUnit> units = new ArrayList<>();
                    dropSpacesCR();
                    while (glance().not(Kind.RR)) {
                        units.add(ImportUnitChannel.make(this, Kind.CR));
                        dropSpacesCR();
                    }
                    Token rr = next();
                    if (rr.not(Kind.RR)) {
                        panic("must be )", rr);
                    }
                    importStatements.add(new ImportMany(symbol, lr, units,rr));
                    break;
                default: panic("wrong " + Kind.IMPORT.value + " statement", next);
            }
            dropSpacesCR();
        }
        if (glance().not(Kind.IMPORT)) { data = new ImportInfo(importStatements); }
        return false;
    }

    public static ImportInfo make(TokenStream stream) {
        return new ImportChannel(stream).make();
    }

}
