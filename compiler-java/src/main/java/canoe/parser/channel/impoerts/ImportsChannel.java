package canoe.parser.channel.impoerts;

import canoe.ast.imports.ImportStatement;
import canoe.ast.imports.ImportStatements;
import canoe.lexer.Kind;
import canoe.lexer.Token;
import canoe.parser.TokenStream;
import canoe.parser.channel.Channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dawn
 */
public class ImportsChannel extends Channel<ImportStatements> {

    private List<ImportStatement> importStatements = new ArrayList<>();

    private ImportsChannel(String name, TokenStream stream) {
        super(name, stream, Arrays.stream(Kind.values())
                .filter(k -> k != Kind.IMPORT)
                .collect(Collectors.toList())
                .toArray(new Kind[]{}));
        removeSpaceOrCR();
        init();
    }

    @Override
    protected boolean eat(Token next) {
        if (next.is(Kind.IMPORT)) {
            // import 开头 尝试解析import语句
            importStatements.add(ImportChannel.produce(this, extend()));
            removeSpaceOrCR();
        }
        if (glance().not(Kind.IMPORT)) { data = new ImportStatements(importStatements); }
        return false;
    }

    public static ImportStatements produce(String name, TokenStream stream) {
        return new ImportsChannel(name, stream).produce();
    }

}
