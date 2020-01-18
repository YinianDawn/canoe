package canoe.lexis;

import java.util.List;

/**
 * @author dawn
 */
public class Tokens {

    private final String fileName;

    private final List<Token> tokens;

    public Tokens(String fileName, List<Token> tokens) {
        this.fileName = fileName;
        this.tokens = tokens;
    }

    public List<Token> getTokens() {
        return tokens;
    }
}
