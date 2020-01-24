package canoe.lexer;

import canoe.compiler.SourceFile;

import java.util.List;

/**
 * @author dawn
 */
public class Tokens {

    private final SourceFile sourceFile;

    private final List<Token> tokens;

    Tokens(SourceFile sourceFile, List<Token> tokens) {
        this.sourceFile = sourceFile;
        this.tokens = tokens;
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }

    public List<Token> getTokens() {
        return tokens;
    }

}
