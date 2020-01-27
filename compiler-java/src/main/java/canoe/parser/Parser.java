package canoe.parser;

import canoe.lexer.Tokens;
import canoe.parser.channel.imports.ImportChannel;
import canoe.parser.channel.packages.PackageChannel;
import canoe.parser.syntax.PackageInfo;
import canoe.parser.syntax.Syntax;
import canoe.parser.syntax.imports.ImportInfo;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class Parser {

    private Tokens tokens;

    public static Syntax parseSyntax(Tokens tokens) {
        return new Parser(tokens).parse();
    }

    private Parser(Tokens tokens) {
        this.tokens = tokens;
    }

    private Syntax parse() {
        TokenStream stream = new TokenStream(tokens);
        if (!stream.has()) {
            panic("tokens can not be empty.");
        }

        PackageInfo packageInfo = PackageChannel.make(stream);
        ImportInfo importInfo = ImportChannel.make(stream);
//        Statements statements = StatementsChannel.produce(tokens.getSourceFile().getName(), stream, Kind.CR);

        return new Syntax(tokens, packageInfo, importInfo);
    }

}
