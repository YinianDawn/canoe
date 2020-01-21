package canoe2.parser;

import canoe2.ast.AST;
import canoe2.ast.PackageName;
import canoe2.ast.expression.Expression;
import canoe2.ast.expression.ExpressionComma;
import canoe2.ast.expression.ExpressionID;
import canoe2.ast.expression.ExpressionParams;
import canoe2.ast.import_statement.ImportStatement;
import canoe2.ast.import_statement.ImportStatementMany;
import canoe2.ast.import_statement.ImportStatementSingle;
import canoe2.ast.import_statement.ImportStatements;
import canoe2.ast.object.Object;
import canoe2.ast.object.*;
import canoe2.ast.statement.*;
import canoe2.ast.statement.desc.*;
import canoe2.lexis.Kind;
import canoe2.lexis.Lexer;
import canoe2.lexis.Token;
import canoe2.lexis.Tokens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static canoe2.util.Util.panic;

/**
 * @author dawn
 */
public class Parser {

    private String fileName;
    private TokenReader reader;

    public static AST parseAst(Tokens tokens) {
        return new Parser(tokens).parse();
    }

    private Parser(Tokens tokens) {
        this.fileName = tokens.getFileName();
        this.reader = new TokenReader(tokens);
    }

    private AST parse() {
        if (!reader.hasNext()) {
            panic("tokens can not be empty.");
        }

        PackageName packageName = parsePackageName();
        ImportStatements importStatements = parseImportStatements();
        Statements statements = parseStatements();

        return new AST(fileName, packageName, importStatements, statements);
    }

    private Statements parseStatements() {
        List<Statement> statements = new LinkedList<>();

        Statement statement = parseStatement();
        while (null != statement) {
            statements.add(statement);
            statement = parseStatement();
        }

        return new Statements(statements);
    }

    private Statement parseStatement() {
        removeSpaceOrCR();

        Token next = reader.nextToken(false);
        switch (next.getKind()) {
            case RETURN: return parseStatementReturn();
            case LOOP: return parseStatementLoop();
            case FOR: return parseStatementFor();
            case IF: return parseStatementIf();
            case MATCH: return parseStatementMatch();

            // } 直接遇到这个符号说明，本轮解析结束，这个符号是上层的结束符
            case RB: return null;

            case NUMBER_DECIMAL:
                return parseStatementExpression();

            case ID:
                IdChannel channel = new IdChannel(fileName, reader, this::parseStatements);
                Expression idExpression = channel.getExpression(false);
                if (null != idExpression) {
                    removeSpace();
                    next = reader.nextToken(false);
                    switch (next.getKind()) {
                        case ADD_ADD:
                        case SUB_SUB:
                            if (!idExpression.endWith(Kind.RR)) { return parseStatementSelfAddOrSub(idExpression); }
                            else { panicToken("can not end by ) in front of " + next.getKind().getKey(), next); }
                            break;
                        case ADD_ASSIGN: case SUB_ASSIGN: case MUL_ASSIGN: case DIV_ASSIGN: case MOD_ASSIGN:
                        case BIT_MOVE_LEFT_ASSIGN: case BIT_MOVE_RIGHT_ASSIGN:
                        case BIT_AND_ASSIGN: case BIT_OR_ASSIGN: case BIT_XOR_ASSIGN:
                            if (!idExpression.endWith(Kind.RR)) { return parseStatementOpAndAssign(idExpression); }
                            else { panicToken("can not end by ) in front of " + next.getKind().getKey(), next); }
                            break;
                        case ADD: case SUB:  case MUL: case DIV: case MOD:
                        case CR:
                            channel.recover();
                            return parseStatementExpression();
                        case ID:
                        case COLON:
                        case LR:
                            channel.recover();
                            return parseStatementDeclaration();

                        default:
                    }
                }
                panicToken("can not be this kind of token.", next);
                break;
            default: panicToken("can not be this kind of token.", next);
        }
        return null;
    }

    // ============== 解析声明语句 ==============

    private StatementDeclaration parseStatementDeclaration() {
        Expression id = new IdChannel(fileName, reader, this::parseStatements).getExpression(true);

        DescOpen descOpen = null;
        DescNative descNative = null;
        DescGoto descGoto = null;
        DescEnum descEnum = null;
        List<DescId> descIds = new ArrayList<>();
        DescStatements descStatements = null;
        DescFunction descFunction = null;
        Token assign = null;
        ExpressionParams expressionParams = null;
        Object body = null;

        Token colon;
        Expression expressionId;
        Token lb;
        Statements statements;
        Expression functionExpresasion;
        Token rb;

        removeSpace();
        Token next = reader.nextToken(false);
        loop:
        while (!next.isCR()) {
            colon = null;
            expressionId = null;
            lb = null;
            statements = null;
            functionExpresasion = null;
            rb = null;
            switch (next.getKind()) {
                case COLON:
                    colon = reader.nextToken();
                    int index = reader.getIndex();
                    next = reader.nextToken();
                    switch (next.getKind()) {

                        // 关键词开头
                        case CANOE:
                            if (reader.nextTokenSkipSpace().getKind() != Kind.DOT) { panicToken("canoe2 can not be desc " + next); break; }

                        case ID:
                            reader.move(index);
                            expressionId = parseObjectExpression().getExpression();
                            descIds.add(new DescId(colon, expressionId));
                            break;
                        case LB:
                            if (null != descStatements) { panicToken("desc statements can only declaration once"); }
                            lb = next;
                            statements = parseStatements();
                            removeSpaceOrCR();
                            rb = reader.nextToken();
                            if (rb.getKind() != Kind.RB) { panicToken("must be }"); }
                            descStatements = new DescStatements(colon, lb, statements, rb);
                            break;
                        case LR:
                            if (null != descFunction) { panicToken("desc function can only declaration once"); }
                            lb = next;
                            functionExpresasion = parseObjectExpression().getExpression();
                            removeSpaceOrCR();
                            rb = reader.nextToken();
                            if (rb.getKind() != Kind.RR) { panicToken("must be )"); }
                            descFunction = new DescFunction(colon, lb, functionExpresasion, rb);
                            break;
                        default: panicToken("what token ? " + next);
                    }
                    break;
                case ASSIGN_FORCE:
                    if (null != assign) { panicToken("assign can only assign once"); }
                    assign = reader.nextToken();
                    break;
                case LR:
                    if (null == descFunction) {
                        // 声明的方法参数
                        lb = reader.nextToken();
                        functionExpresasion = parseObjectExpression().getExpression();
                        removeSpaceOrCR();
                        rb = reader.nextToken();
                        if (rb.getKind() != Kind.RR) { panicToken("must be )"); }
                        descFunction = new DescFunction(null, lb, functionExpresasion, rb);
                        break;
                    } else {
                        // body 的参数
                        lb = reader.nextToken();
                        Expression expression = parseObjectExpression().getExpression();
                        removeSpaceOrCR();
                        rb = reader.nextToken();
                        if (rb.getKind() != Kind.RR) { panicToken("must be )"); }
                        expressionParams = new ExpressionParams(lb, expression, rb);
                        break;
                    }
                case LB:
                    // 一定是方法体
                    lb = reader.nextToken();
                    statements = parseStatements();
                    removeSpaceOrCR();
                    rb = reader.nextToken();
                    if (rb.getKind() != Kind.RB) { panicToken("must be }"); }
                    removeSpace();
                    body = new ObjectStruct(lb, statements, rb);
                    break loop;

                default: panicToken("what token ? " + next);
            }
            removeSpace();
            next = reader.nextToken(false);
        }

        return new StatementDeclaration(id, descOpen, descNative, descGoto, descEnum, descIds, descStatements, descFunction, assign, expressionParams, body);
    }

    // ============== 解析表达式语句 ==============

    private StatementExpression parseStatementExpression() {
        removeSpaceOrCR();
        Expression expression = parseObjectExpression().getExpression();
        removeSpaceOrCR();

        return new StatementExpression(expression);
    }

    // ============== 解析match语句 ==============

    private StatementMatch parseStatementMatch() {
        Token match = reader.nextToken();
        if (match.getKind() != Kind.MATCH) { panicToken("must be match"); }

        removeSpaceOrCR();
        Token with = reader.nextToken(false);
        if (with.getKind() == Kind.COLON_WITH) { with = reader.nextToken(); } else { with = null; }

        removeSpaceOrCR();
        Expression expression = parseObjectExpression().getExpression();

        removeSpaceOrCR();
        Token lb = reader.nextToken();
        if (lb.getKind() != Kind.LB) { panicToken("must be {"); }

        List<StatementMatchClause> clauses = new ArrayList<>();
        StatementMatchClause elseClause = null;

        Token op;
        Expression targetExpression;
        Token colon;
        Token clauseLb;
        Statements statements;
        Token clauseRb;

        Token next = reader.nextTokenSkipSpaceOrCR();
        loop:
        while (null != next) {
            op = null;
            clauseLb = null;
            statements = null;
            clauseRb = null;
            switch (next.getKind()) {
                case GT:
                    removeSpaceOrCR();
                    op = reader.nextToken();
                // 没有比较符号 直接就是个表达式
                case NUMBER_DECIMAL:
                    removeSpaceOrCR();
                    targetExpression = parseObjectExpression().getExpression();
                    removeSpaceOrCR();
                    colon = reader.nextToken();
                    if (colon.getKind() != Kind.COLON) { panicToken("must be :"); }
                    removeSpaceOrCR();
                    next = reader.nextToken(false);
                    switch (next.getKind()) {
                        case BREAK: statements = new Statements(Collections.singletonList(new StatementBreak(reader.nextToken()))); break;
                        case LB:
                            clauseLb = reader.nextToken();
                            statements = parseStatements();
                            removeSpaceOrCR();
                            clauseRb = reader.nextToken();
                            if (clauseRb.getKind() != Kind.RB) { panicToken("must be }");  }
                            break;
                        default: panicToken("can not be", next);
                    }
                    clauses.add(new StatementMatchClause(op, targetExpression, colon, clauseLb, statements, clauseRb));
                    break;
                case ELSE:
                    removeSpaceOrCR();
                    op = reader.nextToken();
                    if (op.getKind() != Kind.ELSE) { panicToken("must be else"); }
                    removeSpaceOrCR();
                    colon = reader.nextToken();
                    if (colon.getKind() != Kind.COLON) { panicToken("must be :"); }
                    removeSpaceOrCR();
                    next = reader.nextToken(false);
                    switch (next.getKind()) {
                        case BREAK: panicToken("else clause does not need break"); break;
                        // id 开头解析一个表达式得了
                        case ID:
                            statements = new Statements(Collections.singletonList(new StatementExpression(parseObjectExpression().getExpression()))); break;
                        case LB:
                            clauseLb = reader.nextToken();
                            statements = parseStatements();
                            removeSpaceOrCR();
                            clauseRb = reader.nextToken();
                            if (clauseRb.getKind() != Kind.RB) { panicToken("must be }");  }
                            break;
                        default: panicToken("can not be", next);
                    }
                    elseClause = new StatementMatchClause(op, null, colon, clauseLb, statements, clauseRb);
                    break loop;
                default: panicToken("can not be", next);
            }
            next = reader.nextTokenSkipSpaceOrCR();
        }

        removeSpaceOrCR();
        Token rb = reader.nextToken();
        if (rb.getKind() != Kind.RB) { panicToken("must be }"); }
        removeSpace();

        return new StatementMatch(match, with, expression, lb, clauses, elseClause, rb);
    }

    // ============== 解析if语句 ==============

    private StatementIf parseStatementIf() {
        Token ifToken = reader.nextToken();
        if (ifToken.getKind() != Kind.IF) { panicToken("must be if"); }
        removeSpaceOrCR();
        Expression expression = parseObjectExpression().getExpression();
        removeSpaceOrCR();
        Token lb = reader.nextToken();
        if (lb.getKind() != Kind.LB) { panicToken("must be {"); }
        Statements statements = parseStatements();
        removeSpaceOrCR();
        Token rb = reader.nextToken();
        if (rb.getKind() != Kind.RB) { panicToken("must be }"); }
        removeSpace();
        Token next = reader.nextTokenSkipSpaceOrCR();
        switch (next.getKind()) {
            case ELSE: case ELSE_IF: break;
            default: return new StatementIf(ifToken, expression, lb, statements, rb,
                        Collections.emptyList(), null, null, null, null);
        }
        removeSpaceOrCR();

        List<StatementElseIf> elseIfs = new ArrayList<>();
        next = reader.nextTokenSkipSpaceOrCR();
        Token elseIf;
        Expression elseIfExpression;
        Token elseIfLb;
        Statements elseIfStatements;
        Token elseIfRb;
        while (next.getKind() == Kind.ELSE_IF) {
            removeSpaceOrCR();
            elseIf = reader.nextToken();
            removeSpaceOrCR();
            elseIfExpression = parseObjectExpression().getExpression();
            removeSpaceOrCR();
            elseIfLb = reader.nextToken();
            if (lb.getKind() != Kind.LB) {
                panicToken("must be {");
            }
            elseIfStatements = parseStatements();
            removeSpaceOrCR();
            elseIfRb = reader.nextToken();
            if (rb.getKind() != Kind.RB) {
                panicToken("must be }");
            }
            removeSpace();
            elseIfs.add(new StatementElseIf(elseIf, elseIfExpression, elseIfLb, elseIfStatements, elseIfRb));
            next = reader.nextTokenSkipSpaceOrCR();
        }
        removeSpace();

        next = reader.nextTokenSkipSpaceOrCR();
        if (next.getKind() != Kind.ELSE) {
            return new StatementIf(ifToken, expression, lb, statements, rb,
                    elseIfs, null, null, null, null);
        }

        removeSpaceOrCR();
        Token elseToken = reader.nextToken();
        if (elseToken.getKind() != Kind.ELSE) {
            panicToken("must be else");
        }
        removeSpaceOrCR();
        Token elseLb = reader.nextToken();
        if (elseLb.getKind() != Kind.LB) {
            panicToken("must be {");
        }
        Statements elseStatements = parseStatements();
        removeSpaceOrCR();
        Token elseRb = reader.nextToken();
        if (elseRb.getKind() != Kind.RB) {
            panicToken("must be }");
        }
        removeSpace();

        return new StatementIf(ifToken, expression, lb, statements, rb,
                elseIfs, elseToken, elseLb, elseStatements, elseRb);
    }

    // ============== 解析运算赋值语句 ==============

    private StatementOpAndAssign parseStatementOpAndAssign(Expression idExpression) {
        Token opAssign = reader.nextToken();
        switch (opAssign.getKind()) {
            case ADD_ASSIGN:
            case SUB_ASSIGN:
            case MUL_ASSIGN:
            case DIV_ASSIGN:
            case MOD_ASSIGN:
            case BIT_MOVE_LEFT_ASSIGN:
            case BIT_MOVE_RIGHT_ASSIGN:
            case BIT_AND_ASSIGN:
            case BIT_OR_ASSIGN:
            case BIT_XOR_ASSIGN: break;
            default: panicToken("op sign must end by =");
        }
        removeSpaceOrCR();
        Expression expression = parseObjectExpression().getExpression();
        removeSpace();
        return new StatementOpAndAssign(idExpression, opAssign, expression);
    }

    // ============== 解析自增自减语句 ==============

    private StatementSelfAddOrSub parseStatementSelfAddOrSub(Expression idExpression) {
        Token rightOp = reader.nextToken();
        switch (rightOp.getKind()) {
            case ADD_ADD:
            case SUB_SUB: break;
            default: panicToken("must be ++ or --");
        }
        removeSpace();
        Token next = reader.nextToken(false);
        if (next.getKind() != Kind.CR) {
            panicToken("must be \\n");
        }
        removeSpace();
        return new StatementSelfAddOrSub(idExpression, rightOp);
    }

    // ============== 解析循环语句 ==============

    private StatementLoop parseStatementLoop() {
        Token loop = reader.nextToken();
        if (loop.getKind() != Kind.LOOP) {
            panicToken("must be loop", loop);
        }
        removeSpace();

        hasNext("loop statement");
        Token colon = reader.nextToken();
        Token id = null;
        Token lb = null;
        switch (colon.getKind()) {
            case COLON:
                hasNext("loop statement");
                id = reader.nextToken();
                if (id.getKind() != Kind.ID) {
                    panicToken("can not be in loop statement", id);
                }
                removeSpaceOrCR();
                lb = reader.nextToken();
                if (lb.getKind() != Kind.LB) {
                    panicToken("can not be in loop statement", lb);
                }
                break;
            case LB:
                lb = colon;
                colon = null;
                id = null;
                break;
            default: panicToken("can not be in loop statement", colon);
        }

        removeSpaceOrCR();

        Statements statements = parseStatements();

        removeSpaceOrCR();

        hasNext("loop statement");
        Token rb = reader.nextToken();
        if (rb.getKind() != Kind.RB) {
            panicToken("must be }", rb);
        }

        removeSpace();

        return new StatementLoop(loop, colon, id, lb, statements, rb);
    }

    private StatementFor parseStatementFor() {
        Token forToken = reader.nextToken();
        if (forToken.getKind() != Kind.FOR) {
            panicToken("must be for", forToken);
        }
        removeSpace();

        hasNext("for statement");
        Token colon = reader.nextToken(false);
        Token id = null;
        if (colon.getKind() == Kind.COLON) {
            colon = reader.nextToken();
            hasNext("for statement");
            id = reader.nextToken();
            if (id.getKind() != Kind.ID) {
                panicToken("can not be in loop statement", id);
            }
        } else {
            colon = null;
        }

        removeSpace();

        ExpressionComma variableExpression = null;

        Expression e = parseObjectExpression().getExpression();
        if (e instanceof ExpressionID) {
            variableExpression = new ExpressionComma(e, null, null);
        } else if (e instanceof ExpressionComma) {
            variableExpression = (ExpressionComma) e;
        }

        if (null == variableExpression) {
            panicToken("can not read variables");
        }

        removeSpace();

        Token in = reader.nextToken();
        if (in.getKind() != Kind.IN) {
            panicToken("must be in");
        }

        removeSpaceOrCR();

        ObjectExpression iteratorObject = parseObjectExpression();
        Expression iteratorExpression = iteratorObject.getExpression();


        removeSpaceOrCR();

        Token lb = reader.nextToken();
        if (lb.getKind() != Kind.LB) {
            panicToken("must be {", lb);
        }

        removeSpaceOrCR();

        Statements statements = parseStatements();

        removeSpaceOrCR();

        hasNext("for statement");
        Token rb = reader.nextToken();
        if (rb.getKind() != Kind.RB) {
            panicToken("must be }", rb);
        }

        removeSpace();

        return new StatementFor(forToken, colon, id, variableExpression, in, iteratorExpression, lb, statements, rb);
    }

    // ============== 解析返回语句 ==============

    private StatementReturn parseStatementReturn() {
        Token returnToken = reader.nextToken();
        removeSpace();

        Token next = reader.nextToken(false);
        switch (next.getKind()) {
            case CR: reader.nextToken(); return new StatementReturn(returnToken, null);

            case LB:
                // 左花括号 尝试解析一个组合对象
            case LS:
                // 左方括号 尝试解析一个数组
            case TRUE:
            case FALSE:
            case NUMBER_HEXADECIMAL:
            case NUMBER_DECIMAL:
            case NUMBER_OCTAL:
            case NUMBER_BINARY:
            case REAL_DECIMAL:
            case STRING:
            case LR:
            case BIT_NOT:
            case ID:
                // 尝试解析一个表达式

                Object object = parseObject();

                // TODO 继续试探 是 组合 还是 数组 构造个返回对象返回回去
                removeSpace();
                List<Object> objects;
                next = reader.nextToken(false);
                switch (next.getKind()) {
                    // } 直接遇到 本轮解析结束
                    case RB:
                    // 只有一个对象
                    case CR: return new StatementReturn(returnToken, object);
                    // , 说明想返回一个数组
                    case COMMA:
                        objects = new ArrayList<>();
                        objects.add(object);
                        List<Token> commas = new ArrayList<>();
                        commas.add(reader.nextToken());
                        parseObjectAndComma(objects, commas);
                        return new StatementReturn(returnToken, new ObjectArrayFixed(objects, commas));
                    // 其他类型就应再尝试解析一个对象
                    case ID:
                    case LB:
                    case LR:
                    case LS:
                        objects = new ArrayList<>();
                        objects.add(object);
                        parseObjectAndComma(objects, null);
                        return new StatementReturn(returnToken, new ObjectCombination(objects));



                    default: panicToken("can not be this kind of token.", next);
                }

                break;


            default: panicToken("can not be this kind of token.", next);
        }

        removeSpaceOrCR();
        return null;
    }

    private Object parseObject() {
        Token next = reader.nextToken(false);
        switch (next.getKind()) {
            // 右花括号 } 和 右方括号 ] 和 换行直接遇到不用解析了
            case RB: case RS: case CR: return null;
            // 左花括号 { 尝试解析一个组合对象
            case LB: return parseObjectStruct();
            // 左方括号 [ 尝试解析一个数组
            case LS: return parseObjectArrayFixed();

            // ID 很麻烦 数组前面也是ID 表达式很多都是ID开头的
            case ID: if (isObjectArrayFixed()) { return parseObjectArrayFixed(); }
            case TRUE:
            case FALSE:
            case NUMBER_HEXADECIMAL:
            case NUMBER_DECIMAL:
            case NUMBER_OCTAL:
            case NUMBER_BINARY:
            case REAL_DECIMAL:
            case STRING:
            case LR:
            case BIT_NOT:
                return parseObjectExpression();

            default: panicToken("can not be this kind of token.", next);
        }
        return null;
    }

    private ObjectExpression parseObjectExpression() {
        // 采用新的方式解析
        Expression expression = new ExpressionChannel(fileName, reader,
                this::parseStatements, this::parseStatementIf, this::parseStatementMatch).getExpression();

        removeSpace();

        return new ObjectExpression(expression);
    }

    private ObjectArrayFixed parseObjectArrayFixed() {
        Token id = reader.nextToken(false);
        switch (id.getKind()) {
            case ID: reader.nextToken(); break;
            case LS: id = null; break;
            default: panicToken("wrong token for array.", id);
        }

        removeSpace();

        Token ls = reader.nextToken();
        if (ls.getKind() != Kind.LS) {
            panicToken("must be [.", ls);
        }

        removeSpace();

        Token number = reader.nextToken(false);
        switch (number.getKind()) {
            case RS: number = null; break;
            case NUMBER_DECIMAL: reader.nextToken(); break;
            default: panicToken("must be integer.", number);
        }

        removeSpace();

        Token rs = reader.nextToken();
        if (rs.getKind() != Kind.RS) {
            panicToken("must be ].", rs);
        }

        removeSpace();

        Token lb = reader.nextToken();
        if (lb.getKind() != Kind.LB) {
            panicToken("must be {.", lb);
        }

        removeSpaceOrCR();

        // 尝试解析多个 对象

        List<Object> objects = new ArrayList<>();
        List<Token> commas = new ArrayList<>();
        parseObjectAndComma(objects, commas);

        removeSpaceOrCR();

        Token rb = reader.nextToken();
        if (rb.getKind() != Kind.RB) {
            panicToken("must be }.", rb);
        }

        removeSpace();
        return new ObjectArrayFixed(id, ls, number, rs, ls, objects, commas, rb);
    }

    private void parseObjectAndComma(List<Object> objects, List<Token> commas) {
        Object object = parseObject();
        Token next;
        while (null != object) {
            objects.add(object);
            removeSpace();
            if (null != commas) {
                next = reader.nextToken(false);
                switch (next.getKind()) {
                    case COMMA: commas.add(reader.nextToken()); break;
                    case RB: return;
                    default: panicToken("can not be this token.", next);
                }
            }
            object = parseObject();
        }
    }

    private boolean isObjectArrayFixed() {
        // 下一个是 [ number ]{  这个形式就是数组
        boolean result = false;
        boolean jump = false;
        int index = reader.getIndex();
        Token last = reader.nextToken();
        if (last.getKind() != Kind.ID) {
            panicToken("can not be this token.", last);
        }
        Token next;
        loop:
        while (reader.hasNext()) {
            next = reader.nextToken();
            switch (next.getKind()) {
                case ID: if (last.getKind() == Kind.ID) { panicToken("can not be ID again.", next); } last = next; break;
                case DOT: if (last.getKind() == Kind.DOT) { panicToken("can not be . again.", next); } last = next; break;
                case SPACES: break;
                case LS:
                    // 已经遇到 [
                    removeSpaceOrCR();
                    next = reader.nextToken();
                    switch (next.getKind()) {
                        case NUMBER_DECIMAL: removeSpaceOrCR(); next = reader.nextToken(); break;
                        case RS: break;
                        default: panicToken("can not be.", next);
                    }
                    if (next.getKind() != Kind.RS) {
                        panicToken("can not be.", next);
                    }
                    removeSpace();
                    next = reader.nextToken();
                    if (next.getKind() == Kind.LB) {
                        result = true;
                    }
                default: jump = true; break loop;
            }
        }
        if (!jump) {
            panicNoMoreContent();
        }
        reader.move(index);
        return result;
    }

    private ObjectStruct parseObjectStruct() {
        Token lb = reader.nextToken();
        if (lb.getKind() != Kind.LB) {
            panicToken("must be {.", lb);
        }

        Statements statements = parseStatements();

        Token rb = reader.nextToken();
        if (rb.getKind() != Kind.RB) {
            panicToken("must be }.", rb);
        }

        removeSpace();
        return new ObjectStruct(lb, statements, rb);
    }

    // ============== 解析导入 ==============

    private ImportStatements parseImportStatements() {
        List<ImportStatement> importStatements = new ArrayList<>();
        ImportStatement importStatement = parseImportStatement();
        while (null != importStatement) {
            importStatements.add(importStatement);
            importStatement = parseImportStatement();
        }
        return new ImportStatements(importStatements);
    }

    private ImportStatement parseImportStatement() {
        if (reader.hasNext() && reader.nextToken(false).getKind() == Kind.IMPORT) {
            Token importToken = reader.nextToken();
            boolean removed = false;
            while (reader.hasNext()) {
                Token next = reader.nextToken(false);
                switch (next.getKind()) {
                    case SPACES: removeSpace(); removed = true; break;
                    case LR: return parseImportStatementMany(importToken);
                    case ID: if (removed) { return parseImportStatementSingle(importToken, false); }
                    default: panicToken("key word import must follow with blank space or (. ", reader.nextToken(false));
                }
            }
            panicNoMoreContent();
        }
        return null;
    }

    private ImportStatementMany parseImportStatementMany(Token importToken) {
        // 取出括号中的内容
        Token lr = reader.nextToken();
        if (lr.getKind() != Kind.LR) {
            panicToken("must be (", lr);
        }

        List<ImportStatementSingle> importStatementSingles = new ArrayList<>();
        ImportStatementSingle importStatementSingle = parseImportStatementSingle(importToken, true);
        while (null != importStatementSingle) {
            importStatementSingles.add(importStatementSingle);
            importStatementSingle = parseImportStatementSingle(importToken, true);
        }

        if (importStatementSingles.isEmpty()) {
            panicToken("must import something.", importToken);
        }

        removeSpaceOrCR();
        Token rr = reader.nextToken();
        if (rr.getKind() != Kind.RR) {
            panicToken("must be )", rr);
        }

        removeSpaceOrCR();
        return new ImportStatementMany(importStatementSingles);
    }

    private ImportStatementSingle parseImportStatementSingle(Token importToken, boolean many) {
        List<Token> tokens = parseImportNameAs(importToken, many);
        if (null == tokens || tokens.isEmpty()) {
            return null;
        }
        Token as = null;
        Token id = null;
        if (2 < tokens.size() && tokens.get(tokens.size() - 2).getKind() == Kind.AS) {
            as = tokens.get(tokens.size() - 2);
            id = tokens.get(tokens.size() - 1);
            tokens = tokens.subList(0, tokens.size() - 2);
        }
        return new ImportStatementSingle(importToken, tokens, as, id);
    }

    private List<Token> parseImportNameAs(Token importToken, boolean many) {
        // ImportNameAS 不允许换行 直接检索所有内容直到遇到换行
        // 第一个token必须是ID 或者其他关键字
        removeSpaceOrCR();
        // 第一个必须是ID
        List<Token> importNameTokens = parseImportName(importToken, many);
        if (null == importNameTokens || importNameTokens.isEmpty()) {
            return importNameTokens;
        }
        // 第一个必须是空格或者换行
        Token next = reader.nextToken();
        switch (next.getKind()) {
            case CR: return importNameTokens;
            case SPACES: break;
            default: panicToken("must be blank space or CR(\\n).", next);
        }
        removeSpace();
        // 第一个必须是AS或者换行
        Token as = reader.nextToken();
        Token id = null;
        switch (as.getKind()) {
            case CR: reader.nextToken(); return importNameTokens;
            case AS:
                removeSpace();
                next = reader.nextToken();
                if (next.getKind() == Kind.ID) {
                    id = next;
                } else {
                    panicToken("token after as must be ID.", next);
                }
                break;
            default: panicToken("must be as or CR(\\n)", as);
        }
        importNameTokens.add(as);
        importNameTokens.add(id);
        removeSpaceOrCR();
        return importNameTokens;
    }

    private List<Token> parseImportName(Token importToken, boolean many) {
        // ImportName 不允许换行 直接检索所有内容直到遇到换行或者空格
        // 第一个token必须是ID 或者其他关键字 最后一个不能是点
        List<Token> tokens = new ArrayList<>();
        Token next;
        boolean first = true;
        while (reader.hasNext()) {
            next = reader.nextToken(false);
            switch (next.getKind()) {
                case DOT: if (first) { panicToken(". can not be first element in import statement.", next); }
                case ID:
                case PACKAGE: case IMPORT: case AS:
                case OPEN: case NATIVE: case GOTO: case ENUM: case CANOE:
                case RETURN:
                case IF: case ELSE: case MATCH: case IN: case WITH:
                case LOOP: case BREAK: case CONTINUE: case FOR:
                case TRUE: case FALSE:
                case INT8: case INT16: case INT32: case INT64:
                case FLOAT32: case FLOAT64:  break;
                case RR: if (!many) { panicToken("this token can not exist in import statement.", next); }
                case SPACES: if (first && next.getKind() == Kind.SPACES) { panicToken("blank space can not be first element in import statement.", next); }
                case CR:
                    if (tokens.isEmpty() && !many) { panicToken("import content can not be empty.", importToken); }
                    if (!tokens.isEmpty() && tokens.get(tokens.size() - 1).getKind() == Kind.DOT) { panicToken(". can not be last element in import statement.", tokens.get(tokens.size() - 1)); }
                    if (!tokens.isEmpty() && Lexer.KINDS.containsKey(tokens.get(tokens.size() - 1).getValue())) { panicToken("package content can not end with key word.", tokens.get(tokens.size() - 1)); }
                    return tokens;

                default: panicToken("this token can not exist in import statement.", next);
            }
            tokens.add(reader.nextToken());
            first = false;
        }
        panicNoMoreContent();
        return null;
    }

    // ============== 解析包 ==============

    private PackageName parsePackageName() {
        PackageName packageName;
        if (reader.nextToken(false).getKind() == Kind.PACKAGE) {
            // 取出packageToken
            Token pack = reader.nextToken();
            removeSpace();

            List<Token> names = new LinkedList<>();
            loop:
            while (reader.hasNext() && !reader.nextToken(false).isSpaces()) {
                Token next = reader.nextToken(false);
                switch (next.getKind()) {
                    case ID: case DOT: break;
                    // 遇到换行符强制认为结束包声明语句
                    case CR: break loop;
                    // 这些关键字允许通过
                    case PACKAGE: case IMPORT: case AS:
                    case OPEN: case NATIVE: case GOTO: case ENUM: case CANOE:
                    case RETURN:
                    case IF: case ELSE: case MATCH: case IN: case WITH:
                    case LOOP: case BREAK: case CONTINUE: case FOR:
                    case TRUE: case FALSE:
                    case INT8: case INT16: case INT32: case INT64:
                    case FLOAT32: case FLOAT64: break;
                    default: panicToken("package name can not contains " + next, next);
                }
                names.add(reader.nextToken());
            }
            if (names.isEmpty()) {
                panicToken("package content can not be empty. ", pack);
            }
            Token first = names.get(0);
            Token last = names.get(names.size() - 1);
            if (first.getKind() == Kind.DOT) {
                panicToken("package content can not start with " + first, first);
            }
            if (last.getKind() == Kind.DOT) {
                panicToken("package content can not end with " + last, last);
            }
            if (Lexer.KINDS.containsKey(last.getValue())) {
                panicToken("package content can not end with key word: " + last, last);
            }
            packageName = new PackageName(pack, names);
        } else {
            packageName = new PackageName(new Token(Kind.PACKAGE, null, 0, 0, 7), Collections.emptyList());
        }
        removeSpaceOrCR();
        return packageName;
    }


    private void removeSpaceOrCR() {
        while (reader.hasNext() && (reader.nextToken(false).isSpaces() || reader.nextToken(false).isCR())) {
            reader.nextToken();
        }
    }

    private void removeSpace() {
        while (reader.hasNext() && reader.nextToken(false).isSpaces()) {
            reader.nextToken();
        }
    }

    private void removeCR() {
        while (reader.hasNext() && reader.nextToken(false).isCR()) {
            reader.nextToken();
        }
    }

    private void hasNext(String tip) {
        if (!reader.hasNext()) {
            panicToken(tip);
        }
    }

    private void panicNoMoreContent() {
        panicToken("no more content. check file '" + fileName + "' please.");
    }

    private void panicToken(String tip, Token token) {
        panic(tip, fileName, token);
    }
    private void panicToken(String tip) {
        panic(tip, fileName, reader.thisToken());
    }
}
