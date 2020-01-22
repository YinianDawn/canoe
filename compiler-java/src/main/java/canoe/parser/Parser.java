package canoe.parser;


import canoe.ast.AST;
import canoe.ast.PackageInfo;
import canoe.ast.imports.ImportStatements;
import canoe.lexer.Tokens;
import canoe.parser.channel.impoerts.ImportsChannel;
import canoe.parser.channel.packages.PackageChannel;

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
//        Statements statements = parseStatements();

//        return new AST(tokens, packageInfo, importStatements, statements);
        return new AST(tokens, packageInfo, null, null);
    }

//    private Statements parseStatements() {
//        stream.removeSpaceOrCR();
//        StatementsChannel channel = new StatementsChannel(tokens.getSourceFile().getName(),
//                stream, -1, Kind.EOF);
//        return channel.get();
//    }

//    private Statement parseStatement() {
//        removeSpaceOrCR();
//
//        Token next = stream.nextToken(false);
//        switch (next.getKind()) {
//            case RETURN: return parseStatementReturn();
//            case LOOP: return parseStatementLoop();
//            case FOR: return parseStatementFor();
//            case IF: return parseStatementIf();
//            case MATCH: return parseStatementMatch();
//
//            // } 直接遇到这个符号说明，本轮解析结束，这个符号是上层的结束符
//            case RB: return null;
//
//            case NUMBER_DECIMAL:
//                return parseStatementExpression();
//
//            case ID:
//                IdChannel channel = new IdChannel(fileName, reader, this::parseStatements);
//                Expression idExpression = channel.getExpression(false);
//                if (null != idExpression) {
//                    removeSpace();
//                    next = stream.nextToken(false);
//                    switch (next.getKind()) {
//                        case ADD_ADD:
//                        case SUB_SUB:
//                            if (!idExpression.endWith(Kind.RR)) { return parseStatementSelfAddOrSub(idExpression); }
//                            else { panicToken("can not end by ) in front of " + next.getKind().getKey(), next); }
//                            break;
//                        case ADD_ASSIGN: case SUB_ASSIGN: case MUL_ASSIGN: case DIV_ASSIGN: case MOD_ASSIGN:
//                        case BIT_MOVE_LEFT_ASSIGN: case BIT_MOVE_RIGHT_ASSIGN:
//                        case BIT_AND_ASSIGN: case BIT_OR_ASSIGN: case BIT_XOR_ASSIGN:
//                            if (!idExpression.endWith(Kind.RR)) { return parseStatementOpAndAssign(idExpression); }
//                            else { panicToken("can not end by ) in front of " + next.getKind().getKey(), next); }
//                            break;
//                        case ADD: case SUB:  case MUL: case DIV: case MOD:
//                        case CR:
//                            channel.recover();
//                            return parseStatementExpression();
//                        case ID:
//                        case COLON:
//                        case LR:
//                            channel.recover();
//                            return parseStatementDeclaration();
//
//                        default:
//                    }
//                }
//                panicToken("can not be this is of token.", next);
//                break;
//            default: panicToken("can not be this is of token.", next);
//        }
//        return null;
//    }
//
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
//
//    // ============== 解析表达式语句 ==============
//
//    private StatementExpression parseStatementExpression() {
//        removeSpaceOrCR();
//        Expression expression = parseObjectExpression().getExpression();
//        removeSpaceOrCR();
//
//        return new StatementExpression(expression);
//    }
//
//    // ============== 解析match语句 ==============
//
//    private StatementMatch parseStatementMatch() {
//        Token match = stream.nextToken();
//        if (match.getKind() != Kind.MATCH) { panicToken("must be match"); }
//
//        removeSpaceOrCR();
//        Token with = stream.nextToken(false);
//        if (with.getKind() == Kind.COLON_WITH) { with = stream.nextToken(); } else { with = null; }
//
//        removeSpaceOrCR();
//        Expression expression = parseObjectExpression().getExpression();
//
//        removeSpaceOrCR();
//        Token lb = stream.nextToken();
//        if (lb.getKind() != Kind.LB) { panicToken("must be {"); }
//
//        List<StatementMatchClause> clauses = new ArrayList<>();
//        StatementMatchClause elseClause = null;
//
//        Token op;
//        Expression targetExpression;
//        Token colon;
//        Token clauseLb;
//        Statements statements;
//        Token clauseRb;
//
//        Token next = stream.nextTokenSkipSpaceOrCR();
//        loop:
//        while (null != next) {
//            op = null;
//            clauseLb = null;
//            statements = null;
//            clauseRb = null;
//            switch (next.getKind()) {
//                case GT:
//                    removeSpaceOrCR();
//                    op = stream.nextToken();
//                // 没有比较符号 直接就是个表达式
//                case NUMBER_DECIMAL:
//                    removeSpaceOrCR();
//                    targetExpression = parseObjectExpression().getExpression();
//                    removeSpaceOrCR();
//                    colon = stream.nextToken();
//                    if (colon.getKind() != Kind.COLON) { panicToken("must be :"); }
//                    removeSpaceOrCR();
//                    next = stream.nextToken(false);
//                    switch (next.getKind()) {
//                        case BREAK: statements = new Statements(Collections.singletonList(new StatementBreak(stream.nextToken()))); break;
//                        case LB:
//                            clauseLb = stream.nextToken();
//                            statements = parseStatements();
//                            removeSpaceOrCR();
//                            clauseRb = stream.nextToken();
//                            if (clauseRb.getKind() != Kind.RB) { panicToken("must be }");  }
//                            break;
//                        default: panicToken("can not be", next);
//                    }
//                    clauses.add(new StatementMatchClause(op, targetExpression, colon, clauseLb, statements, clauseRb));
//                    break;
//                case ELSE:
//                    removeSpaceOrCR();
//                    op = stream.nextToken();
//                    if (op.getKind() != Kind.ELSE) { panicToken("must be else"); }
//                    removeSpaceOrCR();
//                    colon = stream.nextToken();
//                    if (colon.getKind() != Kind.COLON) { panicToken("must be :"); }
//                    removeSpaceOrCR();
//                    next = stream.nextToken(false);
//                    switch (next.getKind()) {
//                        case BREAK: panicToken("else clause does not need break"); break;
//                        // id 开头解析一个表达式得了
//                        case ID:
//                            statements = new Statements(Collections.singletonList(new StatementExpression(parseObjectExpression().getExpression()))); break;
//                        case LB:
//                            clauseLb = stream.nextToken();
//                            statements = parseStatements();
//                            removeSpaceOrCR();
//                            clauseRb = stream.nextToken();
//                            if (clauseRb.getKind() != Kind.RB) { panicToken("must be }");  }
//                            break;
//                        default: panicToken("can not be", next);
//                    }
//                    elseClause = new StatementMatchClause(op, null, colon, clauseLb, statements, clauseRb);
//                    break loop;
//                default: panicToken("can not be", next);
//            }
//            next = stream.nextTokenSkipSpaceOrCR();
//        }
//
//        removeSpaceOrCR();
//        Token rb = stream.nextToken();
//        if (rb.getKind() != Kind.RB) { panicToken("must be }"); }
//        removeSpace();
//
//        return new StatementMatch(match, with, expression, lb, clauses, elseClause, rb);
//    }
//
//    // ============== 解析if语句 ==============
//
//    private StatementIf parseStatementIf() {
//        Token ifToken = stream.nextToken();
//        if (ifToken.getKind() != Kind.IF) { panicToken("must be if"); }
//        removeSpaceOrCR();
//        Expression expression = parseObjectExpression().getExpression();
//        removeSpaceOrCR();
//        Token lb = stream.nextToken();
//        if (lb.getKind() != Kind.LB) { panicToken("must be {"); }
//        Statements statements = parseStatements();
//        removeSpaceOrCR();
//        Token rb = stream.nextToken();
//        if (rb.getKind() != Kind.RB) { panicToken("must be }"); }
//        removeSpace();
//        Token next = stream.nextTokenSkipSpaceOrCR();
//        switch (next.getKind()) {
//            case ELSE: case ELSE_IF: break;
//            default: return new StatementIf(ifToken, expression, lb, statements, rb,
//                        Collections.emptyList(), null, null, null, null);
//        }
//        removeSpaceOrCR();
//
//        List<StatementElseIf> elseIfs = new ArrayList<>();
//        next = stream.nextTokenSkipSpaceOrCR();
//        Token elseIf;
//        Expression elseIfExpression;
//        Token elseIfLb;
//        Statements elseIfStatements;
//        Token elseIfRb;
//        while (next.getKind() == Kind.ELSE_IF) {
//            removeSpaceOrCR();
//            elseIf = stream.nextToken();
//            removeSpaceOrCR();
//            elseIfExpression = parseObjectExpression().getExpression();
//            removeSpaceOrCR();
//            elseIfLb = stream.nextToken();
//            if (lb.getKind() != Kind.LB) {
//                panicToken("must be {");
//            }
//            elseIfStatements = parseStatements();
//            removeSpaceOrCR();
//            elseIfRb = stream.nextToken();
//            if (rb.getKind() != Kind.RB) {
//                panicToken("must be }");
//            }
//            removeSpace();
//            elseIfs.add(new StatementElseIf(elseIf, elseIfExpression, elseIfLb, elseIfStatements, elseIfRb));
//            next = stream.nextTokenSkipSpaceOrCR();
//        }
//        removeSpace();
//
//        next = stream.nextTokenSkipSpaceOrCR();
//        if (next.getKind() != Kind.ELSE) {
//            return new StatementIf(ifToken, expression, lb, statements, rb,
//                    elseIfs, null, null, null, null);
//        }
//
//        removeSpaceOrCR();
//        Token elseToken = stream.nextToken();
//        if (elseToken.getKind() != Kind.ELSE) {
//            panicToken("must be else");
//        }
//        removeSpaceOrCR();
//        Token elseLb = stream.nextToken();
//        if (elseLb.getKind() != Kind.LB) {
//            panicToken("must be {");
//        }
//        Statements elseStatements = parseStatements();
//        removeSpaceOrCR();
//        Token elseRb = stream.nextToken();
//        if (elseRb.getKind() != Kind.RB) {
//            panicToken("must be }");
//        }
//        removeSpace();
//
//        return new StatementIf(ifToken, expression, lb, statements, rb,
//                elseIfs, elseToken, elseLb, elseStatements, elseRb);
//    }
//
//    // ============== 解析运算赋值语句 ==============
//
//    private StatementOpAndAssign parseStatementOpAndAssign(Expression idExpression) {
//        Token opAssign = stream.nextToken();
//        switch (opAssign.getKind()) {
//            case ADD_ASSIGN:
//            case SUB_ASSIGN:
//            case MUL_ASSIGN:
//            case DIV_ASSIGN:
//            case MOD_ASSIGN:
//            case BIT_MOVE_LEFT_ASSIGN:
//            case BIT_MOVE_RIGHT_ASSIGN:
//            case BIT_AND_ASSIGN:
//            case BIT_OR_ASSIGN:
//            case BIT_XOR_ASSIGN: break;
//            default: panicToken("op sign must end by =");
//        }
//        removeSpaceOrCR();
//        Expression expression = parseObjectExpression().getExpression();
//        removeSpace();
//        return new StatementOpAndAssign(idExpression, opAssign, expression);
//    }
//
//    // ============== 解析自增自减语句 ==============
//
//    private StatementSelfAddOrSub parseStatementSelfAddOrSub(Expression idExpression) {
//        Token rightOp = stream.nextToken();
//        switch (rightOp.getKind()) {
//            case ADD_ADD:
//            case SUB_SUB: break;
//            default: panicToken("must be ++ or --");
//        }
//        removeSpace();
//        Token next = stream.nextToken(false);
//        if (next.getKind() != Kind.CR) {
//            panicToken("must be \\n");
//        }
//        removeSpace();
//        return new StatementSelfAddOrSub(idExpression, rightOp);
//    }
//
//    // ============== 解析循环语句 ==============
//
//    private StatementLoop parseStatementLoop() {
//        Token loop = stream.nextToken();
//        if (loop.getKind() != Kind.LOOP) {
//            panicToken("must be loop", loop);
//        }
//        removeSpace();
//
//        hasNext("loop statement");
//        Token colon = stream.nextToken();
//        Token id = null;
//        Token lb = null;
//        switch (colon.getKind()) {
//            case COLON:
//                hasNext("loop statement");
//                id = stream.nextToken();
//                if (id.getKind() != Kind.ID) {
//                    panicToken("can not be in loop statement", id);
//                }
//                removeSpaceOrCR();
//                lb = stream.nextToken();
//                if (lb.getKind() != Kind.LB) {
//                    panicToken("can not be in loop statement", lb);
//                }
//                break;
//            case LB:
//                lb = colon;
//                colon = null;
//                id = null;
//                break;
//            default: panicToken("can not be in loop statement", colon);
//        }
//
//        removeSpaceOrCR();
//
//        Statements statements = parseStatements();
//
//        removeSpaceOrCR();
//
//        hasNext("loop statement");
//        Token rb = stream.nextToken();
//        if (rb.getKind() != Kind.RB) {
//            panicToken("must be }", rb);
//        }
//
//        removeSpace();
//
//        return new StatementLoop(loop, colon, id, lb, statements, rb);
//    }
//
//    private StatementFor parseStatementFor() {
//        Token forToken = stream.nextToken();
//        if (forToken.getKind() != Kind.FOR) {
//            panicToken("must be for", forToken);
//        }
//        removeSpace();
//
//        hasNext("for statement");
//        Token colon = stream.nextToken(false);
//        Token id = null;
//        if (colon.getKind() == Kind.COLON) {
//            colon = stream.nextToken();
//            hasNext("for statement");
//            id = stream.nextToken();
//            if (id.getKind() != Kind.ID) {
//                panicToken("can not be in loop statement", id);
//            }
//        } else {
//            colon = null;
//        }
//
//        removeSpace();
//
//        ExpressionComma variableExpression = null;
//
//        Expression e = parseObjectExpression().getExpression();
//        if (e instanceof ExpressionID) {
//            variableExpression = new ExpressionComma(e, null, null);
//        } else if (e instanceof ExpressionComma) {
//            variableExpression = (ExpressionComma) e;
//        }
//
//        if (null == variableExpression) {
//            panicToken("can not read variables");
//        }
//
//        removeSpace();
//
//        Token in = stream.nextToken();
//        if (in.getKind() != Kind.IN) {
//            panicToken("must be in");
//        }
//
//        removeSpaceOrCR();
//
//        ObjectExpression iteratorObject = parseObjectExpression();
//        Expression iteratorExpression = iteratorObject.getExpression();
//
//
//        removeSpaceOrCR();
//
//        Token lb = stream.nextToken();
//        if (lb.getKind() != Kind.LB) {
//            panicToken("must be {", lb);
//        }
//
//        removeSpaceOrCR();
//
//        Statements statements = parseStatements();
//
//        removeSpaceOrCR();
//
//        hasNext("for statement");
//        Token rb = stream.nextToken();
//        if (rb.getKind() != Kind.RB) {
//            panicToken("must be }", rb);
//        }
//
//        removeSpace();
//
//        return new StatementFor(forToken, colon, id, variableExpression, in, iteratorExpression, lb, statements, rb);
//    }
//
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

    // ============== 解析导入 ==============

//    private ImportStatements parseImportStatements() {
//        Token importToken = stream.glance();
//        if (importToken.not(Kind.IMPORT)) {
//            return new ImportStatements(Collections.emptyList());
//        }
//        ImportChannel channel = new ImportChannel(tokens.getSourceFile().getName(), stream);
//        return channel.get();
//    }

}
