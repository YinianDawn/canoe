//package canoe.parser.syntax.statement;
//
//import canoe.lexer.Token;
//
///**
// * @author dawn
// */
//public class StatementGoto implements Statement {
//
//    private final Token GOTO;
//
//    private final Token label;
//
//    public StatementGoto(Token symbol, Token label) {
//        this.GOTO = symbol;
//        this.label = label;
//    }
//
//    @Override
//    public Token first() {
//        return GOTO;
//    }
//
//    @Override
//    public Token last() {
//        return label;
//    }
//}
