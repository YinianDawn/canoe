//package canoe.parser.syntax.expression.call;
//
//import canoe.lexer.Token;
//import canoe.parser.syntax.expression.Expression;
//import canoe.parser.syntax.expression.pair.ExpressionRound;
//
///**
// * @author dawn
// */
//public class ExpressionOverloadCall implements Expression {
//
//    private final Token overload;
//
//    private final ExpressionRound round;
//
//    public ExpressionOverloadCall(Token overload, ExpressionRound round) {
//        this.overload = overload;
//        this.round = round;
//    }
//
//    @Override
//    public Token first() {
//        return overload;
//    }
//
//    @Override
//    public Token last() {
//        return round.last();
//    }
//}
