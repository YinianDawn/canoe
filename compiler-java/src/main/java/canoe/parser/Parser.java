package canoe.parser;

import canoe.lexer.Tokens;
import canoe.parser.channel.packages.PackageChannel;
import canoe.parser.syntax.PackageInfo;
import canoe.parser.syntax.Syntax;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class Parser {

    private Tokens tokens;

    private TokenStream stream;

    public static Syntax parseSyntax(Tokens tokens) {
        return new Parser(tokens).parse();
    }

    private Parser(Tokens tokens) {
        this.tokens = tokens;
    }

    private Syntax parse() {
        this.stream = new TokenStream(tokens);
        if (!stream.has()) {
            panic("tokens can not be empty.");
        }

        PackageInfo packageInfo = PackageChannel.make(stream);
//        ImportStatements importStatements = ImportsChannel.produce(tokens.getSourceFile().getName(), stream);
//        Statements statements = StatementsChannel.produce(tokens.getSourceFile().getName(), stream, Kind.CR);

        return new Syntax(tokens, packageInfo);
    }

}
