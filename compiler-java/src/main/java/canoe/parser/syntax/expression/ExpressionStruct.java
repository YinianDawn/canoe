//package canoe.parser.syntax.expression;
//
//
//import canoe.lexer.Token;
//import canoe.parser.syntax.Statements;
//
///**
// * @author dawn
// */
//public class ExpressionStruct implements Expression {
//
//    private Token LB;
//    private Statements statements;
//    private Token RB;
//
//    public ExpressionStruct(Token lb, Statements statements, Token rb) {
//        this.LB = lb;
//        this.statements = statements;
//        this.RB = rb;
//    }
//
//    @Override
//    public Token first() {
//        return LB;
//    }
//
//    @Override
//    public Token last() {
//        return RB;
//    }
//}
