//package canoe.parser.syntax.statement;
//
//import canoe.lexer.Token;
//import canoe.parser.syntax.expression.Expression;
//
///**
// * @author dawn
// */
//public class StatementAssign implements Statement {
//
//    private final Expression id;
//
//    private final Token assign;
//
//    private final Expression expression;
//
//    public StatementAssign(Expression id, Token assign, Expression expression) {
//        this.id = id;
//        this.assign = assign;
//        this.expression = expression;
//    }
//
//    @Override
//    public Token first() {
//        return id.first();
//    }
//
//    @Override
//    public Token last() {
//        return expression.last();
//    }
//}
