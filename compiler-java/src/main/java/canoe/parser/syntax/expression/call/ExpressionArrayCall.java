//package canoe.parser.syntax.expression.call;
//
//import canoe.lexer.Token;
//import canoe.parser.syntax.expression.Expression;
//import canoe.parser.syntax.expression.pair.ExpressionSquare;
//
///**
// * @author dawn
// */
//public class ExpressionArrayCall implements Expression {
//
//    private final Expression id;
//
//    private final ExpressionSquare square;
//
//    public ExpressionArrayCall(Expression id, ExpressionSquare square) {
//        this.id = id;
//        this.square = square;
//    }
//
//    @Override
//    public Token first() {
//        return id.first();
//    }
//
//    @Override
//    public Token last() {
//        return square.last();
//    }
//}
