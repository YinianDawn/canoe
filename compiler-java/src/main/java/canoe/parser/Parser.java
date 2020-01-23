package canoe.parser;


import canoe.ast.AST;
import canoe.ast.PackageInfo;
import canoe.ast.imports.ImportStatements;
import canoe.ast.statement.Statements;
import canoe.lexer.Kind;
import canoe.lexer.Tokens;
import canoe.parser.channel.impoerts.ImportsChannel;
import canoe.parser.channel.packages.PackageChannel;
import canoe.parser.channel.statement.StatementsChannel;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class Parser {

    private Tokens tokens;

    private TokenStream stream;

    public static AST parseAST(Tokens tokens) {
        return new Parser(tokens).parse();
    }

    private Parser(Tokens tokens) {
        this.tokens = tokens;
        this.stream = new TokenStream(tokens);
    }

    private AST parse() {
        if (!stream.has()) {
            panic("tokens can not be empty.");
        }

        PackageInfo packageInfo = PackageChannel.produce(tokens.getSourceFile().getName(), stream);
        ImportStatements importStatements = ImportsChannel.produce(tokens.getSourceFile().getName(), stream);
        Statements statements = StatementsChannel.produce(tokens.getSourceFile().getName(), stream, Kind.CR);

        return new AST(tokens, packageInfo, importStatements, statements);
    }


//    // ============== 解析声明语句 ==============
//
//    private StatementDeclaration parseStatementDeclaration() {
//        Expression id = new IdChannel(fileName, reader, this::parseStatements).getExpression(true);
//
//        DescOpen descOpen = null;
//        DescNative descNative = null;
//        DescGoto descGoto = null;
//        DescEnum descEnum = null;
//        List<DescId> descIds = new ArrayList<>();
//        DescStatements descStatements = null;
//        DescFunction descFunction = null;
//        Token assign = null;
//        ExpressionParams expressionParams = null;
//        Object body = null;
//
//        Token colon;
//        Expression expressionId;
//        Token lb;
//        Statements statements;
//        Expression functionExpresasion;
//        Token rb;
//
//        removeSpace();
//        Token next = stream.nextToken(false);
//        loop:
//        while (!next.isCR()) {
//            colon = null;
//            expressionId = null;
//            lb = null;
//            statements = null;
//            functionExpresasion = null;
//            rb = null;
//            switch (next.getKind()) {
//                case COLON:
//                    colon = stream.nextToken();
//                    int index = stream.getIndex();
//                    next = stream.nextToken();
//                    switch (next.getKind()) {
//
//                        // 关键词开头
//                        case CANOE:
//                            if (stream.nextTokenSkipSpace().getKind() != Kind.DOT) { panicToken("canoe2 can not be desc " + next); break; }
//
//                        case ID:
//                            stream.move(index);
//                            expressionId = parseObjectExpression().getExpression();
//                            descIds.add(new DescId(colon, expressionId));
//                            break;
//                        case LB:
//                            if (null != descStatements) { panicToken("desc statements can only declaration once"); }
//                            lb = next;
//                            statements = parseStatements();
//                            removeSpaceOrCR();
//                            rb = stream.nextToken();
//                            if (rb.getKind() != Kind.RB) { panicToken("must be }"); }
//                            descStatements = new DescStatements(colon, lb, statements, rb);
//                            break;
//                        case LR:
//                            if (null != descFunction) { panicToken("desc function can only declaration once"); }
//                            lb = next;
//                            functionExpresasion = parseObjectExpression().getExpression();
//                            removeSpaceOrCR();
//                            rb = stream.nextToken();
//                            if (rb.getKind() != Kind.RR) { panicToken("must be )"); }
//                            descFunction = new DescFunction(colon, lb, functionExpresasion, rb);
//                            break;
//                        default: panicToken("what token ? " + next);
//                    }
//                    break;
//                case ASSIGN_FORCE:
//                    if (null != assign) { panicToken("assign can only assign once"); }
//                    assign = stream.nextToken();
//                    break;
//                case LR:
//                    if (null == descFunction) {
//                        // 声明的方法参数
//                        lb = stream.nextToken();
//                        functionExpresasion = parseObjectExpression().getExpression();
//                        removeSpaceOrCR();
//                        rb = stream.nextToken();
//                        if (rb.getKind() != Kind.RR) { panicToken("must be )"); }
//                        descFunction = new DescFunction(null, lb, functionExpresasion, rb);
//                        break;
//                    } else {
//                        // body 的参数
//                        lb = stream.nextToken();
//                        Expression expression = parseObjectExpression().getExpression();
//                        removeSpaceOrCR();
//                        rb = stream.nextToken();
//                        if (rb.getKind() != Kind.RR) { panicToken("must be )"); }
//                        expressionParams = new ExpressionParams(lb, expression, rb);
//                        break;
//                    }
//                case LB:
//                    // 一定是方法体
//                    lb = stream.nextToken();
//                    statements = parseStatements();
//                    removeSpaceOrCR();
//                    rb = stream.nextToken();
//                    if (rb.getKind() != Kind.RB) { panicToken("must be }"); }
//                    removeSpace();
//                    body = new ObjectStruct(lb, statements, rb);
//                    break loop;
//
//                default: panicToken("what token ? " + next);
//            }
//            removeSpace();
//            next = stream.nextToken(false);
//        }
//
//        return new StatementDeclaration(id, descOpen, descNative, descGoto, descEnum, descIds, descStatements, descFunction, assign, expressionParams, body);
//    }


//    // ============== 解析返回语句 ==============
//
//    private StatementReturn parseStatementReturn() {
//        Token returnToken = stream.nextToken();
//        removeSpace();
//
//        Token next = stream.nextToken(false);
//        switch (next.getKind()) {
//            case CR: stream.nextToken(); return new StatementReturn(returnToken, null);
//
//            case LB:
//                // 左花括号 尝试解析一个组合对象
//            case LS:
//                // 左方括号 尝试解析一个数组
//            case TRUE:
//            case FALSE:
//            case NUMBER_HEXADECIMAL:
//            case NUMBER_DECIMAL:
//            case NUMBER_OCTAL:
//            case NUMBER_BINARY:
//            case REAL_DECIMAL:
//            case STRING:
//            case LR:
//            case BIT_NOT:
//            case ID:
//                // 尝试解析一个表达式
//
//                Object object = parseObject();
//
//                // TODO 继续试探 是 组合 还是 数组 构造个返回对象返回回去
//                removeSpace();
//                List<Object> objects;
//                next = stream.nextToken(false);
//                switch (next.getKind()) {
//                    // } 直接遇到 本轮解析结束
//                    case RB:
//                    // 只有一个对象
//                    case CR: return new StatementReturn(returnToken, object);
//                    // , 说明想返回一个数组
//                    case COMMA:
//                        objects = new ArrayList<>();
//                        objects.add(object);
//                        List<Token> commas = new ArrayList<>();
//                        commas.add(stream.nextToken());
//                        parseObjectAndComma(objects, commas);
//                        return new StatementReturn(returnToken, new ObjectArrayFixed(objects, commas));
//                    // 其他类型就应再尝试解析一个对象
//                    case ID:
//                    case LB:
//                    case LR:
//                    case LS:
//                        objects = new ArrayList<>();
//                        objects.add(object);
//                        parseObjectAndComma(objects, null);
//                        return new StatementReturn(returnToken, new ObjectCombination(objects));
//
//
//
//                    default: panicToken("can not be this is of token.", next);
//                }
//
//                break;
//
//
//            default: panicToken("can not be this is of token.", next);
//        }
//
//        removeSpaceOrCR();
//        return null;
//    }
//
//    private Object parseObject() {
//        Token next = stream.nextToken(false);
//        switch (next.getKind()) {
//            // 右花括号 } 和 右方括号 ] 和 换行直接遇到不用解析了
//            case RB: case RS: case CR: return null;
//            // 左花括号 { 尝试解析一个组合对象
//            case LB: return parseObjectStruct();
//            // 左方括号 [ 尝试解析一个数组
//            case LS: return parseObjectArrayFixed();
//
//            // ID 很麻烦 数组前面也是ID 表达式很多都是ID开头的
//            case ID: if (isObjectArrayFixed()) { return parseObjectArrayFixed(); }
//            case TRUE:
//            case FALSE:
//            case NUMBER_HEXADECIMAL:
//            case NUMBER_DECIMAL:
//            case NUMBER_OCTAL:
//            case NUMBER_BINARY:
//            case REAL_DECIMAL:
//            case STRING:
//            case LR:
//            case BIT_NOT:
//                return parseObjectExpression();
//
//            default: panicToken("can not be this is of token.", next);
//        }
//        return null;
//    }
//
//    private ObjectExpression parseObjectExpression() {
//        // 采用新的方式解析
//        Expression expression = new ExpressionChannel(fileName, reader,
//                this::parseStatements, this::parseStatementIf, this::parseStatementMatch).getExpression();
//
//        removeSpace();
//
//        return new ObjectExpression(expression);
//    }
//
//    private ObjectArrayFixed parseObjectArrayFixed() {
//        Token id = stream.nextToken(false);
//        switch (id.getKind()) {
//            case ID: stream.nextToken(); break;
//            case LS: id = null; break;
//            default: panicToken("wrong token for array.", id);
//        }
//
//        removeSpace();
//
//        Token ls = stream.nextToken();
//        if (ls.getKind() != Kind.LS) {
//            panicToken("must be [.", ls);
//        }
//
//        removeSpace();
//
//        Token number = stream.nextToken(false);
//        switch (number.getKind()) {
//            case RS: number = null; break;
//            case NUMBER_DECIMAL: stream.nextToken(); break;
//            default: panicToken("must be integer.", number);
//        }
//
//        removeSpace();
//
//        Token rs = stream.nextToken();
//        if (rs.getKind() != Kind.RS) {
//            panicToken("must be ].", rs);
//        }
//
//        removeSpace();
//
//        Token lb = stream.nextToken();
//        if (lb.getKind() != Kind.LB) {
//            panicToken("must be {.", lb);
//        }
//
//        removeSpaceOrCR();
//
//        // 尝试解析多个 对象
//
//        List<Object> objects = new ArrayList<>();
//        List<Token> commas = new ArrayList<>();
//        parseObjectAndComma(objects, commas);
//
//        removeSpaceOrCR();
//
//        Token rb = stream.nextToken();
//        if (rb.getKind() != Kind.RB) {
//            panicToken("must be }.", rb);
//        }
//
//        removeSpace();
//        return new ObjectArrayFixed(id, ls, number, rs, ls, objects, commas, rb);
//    }
//
//    private void parseObjectAndComma(List<Object> objects, List<Token> commas) {
//        Object object = parseObject();
//        Token next;
//        while (null != object) {
//            objects.add(object);
//            removeSpace();
//            if (null != commas) {
//                next = stream.nextToken(false);
//                switch (next.getKind()) {
//                    case COMMA: commas.add(stream.nextToken()); break;
//                    case RB: return;
//                    default: panicToken("can not be this token.", next);
//                }
//            }
//            object = parseObject();
//        }
//    }
//
//    private boolean isObjectArrayFixed() {
//        // 下一个是 [ number ]{  这个形式就是数组
//        boolean result = false;
//        boolean jump = false;
//        int index = stream.getIndex();
//        Token last = stream.nextToken();
//        if (last.getKind() != Kind.ID) {
//            panicToken("can not be this token.", last);
//        }
//        Token next;
//        loop:
//        while (stream.hasNext()) {
//            next = stream.nextToken();
//            switch (next.getKind()) {
//                case ID: if (last.getKind() == Kind.ID) { panicToken("can not be ID again.", next); } last = next; break;
//                case DOT: if (last.getKind() == Kind.DOT) { panicToken("can not be . again.", next); } last = next; break;
//                case SPACES: break;
//                case LS:
//                    // 已经遇到 [
//                    removeSpaceOrCR();
//                    next = stream.nextToken();
//                    switch (next.getKind()) {
//                        case NUMBER_DECIMAL: removeSpaceOrCR(); next = stream.nextToken(); break;
//                        case RS: break;
//                        default: panicToken("can not be.", next);
//                    }
//                    if (next.getKind() != Kind.RS) {
//                        panicToken("can not be.", next);
//                    }
//                    removeSpace();
//                    next = stream.nextToken();
//                    if (next.getKind() == Kind.LB) {
//                        result = true;
//                    }
//                default: jump = true; break loop;
//            }
//        }
//        if (!jump) {
//            panicNoMoreContent();
//        }
//        stream.move(index);
//        return result;
//    }
//
//    private ObjectStruct parseObjectStruct() {
//        Token lb = stream.nextToken();
//        if (lb.getKind() != Kind.LB) {
//            panicToken("must be {.", lb);
//        }
//
//        Statements statements = parseStatements();
//
//        Token rb = stream.nextToken();
//        if (rb.getKind() != Kind.RB) {
//            panicToken("must be }.", rb);
//        }
//
//        removeSpace();
//        return new ObjectStruct(lb, statements, rb);
//    }

}
