//package canoe.parser.syntax.expression;
//
//import canoe.lexer.Token;
//
///**
// * @author dawn
// */
//public class ExpressionOpLeft implements Expression {
//
//    private final Token op;
//    private final Expression expression;
//
//
//    public ExpressionOpLeft(Token op, Expression expression) {
//        this.op = op;
//        this.expression = expression;
//    }
//
//    @Override
//    public Token first() {
//        return op;
//    }
//
//    @Override
//    public Token last() {
//        return expression.last();
//    }
//}
