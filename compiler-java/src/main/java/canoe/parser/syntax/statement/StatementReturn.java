//package canoe.parser.syntax.statement;
//
//
//import canoe.lexer.Token;
//import canoe.parser.syntax.expression.Expression;
//
///**
// * @author dawn
// */
//public class StatementReturn implements Statement {
//
//    private final Token RETURN;
//    private final Expression expression;
//
//    public StatementReturn(Token symbol, Expression expression) {
//        this.RETURN = symbol;
//        this.expression = expression;
//    }
//
//    @Override
//    public Token first() {
//        return RETURN;
//    }
//
//    @Override
//    public Token last() {
//        return expression.last();
//    }
//}
