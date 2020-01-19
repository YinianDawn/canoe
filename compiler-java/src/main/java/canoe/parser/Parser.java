package canoe.parser;

import canoe.ast.*;
import canoe.ast.expression.Expression;
import canoe.ast.import_statement.ImportStatement;
import canoe.ast.import_statement.ImportStatementMany;
import canoe.ast.import_statement.ImportStatementSingle;
import canoe.ast.import_statement.ImportStatements;
import canoe.ast.object.Object;
import canoe.ast.object.ObjectArray;
import canoe.ast.object.ObjectExpression;
import canoe.ast.object.ObjectStruct;
import canoe.ast.statement.Statement;
import canoe.ast.statement.StatementReturn;
import canoe.ast.statement.Statements;
import canoe.lexis.Kind;
import canoe.lexis.Lexer;
import canoe.lexis.Token;
import canoe.lexis.Tokens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static canoe.util.Util.panic;

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

//        Compiler.printTokens(new Tokens(reader.getFileName(), reader.getRemainTokens()));

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

            case RB:
                // }
                return null;
            default: panicToken("can not be this kind of token.", next);
        }
        return null;
    }

    private Statement parseStatementReturn() {
        Token returnToken = reader.nextToken();
        removeSpace();

        Token next = reader.nextToken(false);
        switch (next.getKind()) {
            case CR: reader.nextToken(); return new StatementReturn(returnToken, null);

            case LB:
                // 左花括号 尝试解析一个组合对象
            case LS:
                // 左方括号 尝试解析一个数组
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
                next = reader.nextToken(false);
                switch (next.getKind()) {
                    case CR:
                        // 只有一个对象
                        reader.nextToken();
                        return new StatementReturn(returnToken, object);

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
            // 右花括号 } 和 右方括号 ] 直接遇到不用解析了
            case RB:
            case RS: return null;
            // 左花括号 { 尝试解析一个组合对象
            case LB: return parseObjectStruct();
            // 左方括号 [ 尝试解析一个数组
            case LS: return parseObjectArray();

            // ID 很麻烦 数组前面也是ID 表达式很多都是ID开头的
            case ID:
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

    private Object parseObjectExpression() {
        // 采用新的方式解析
        ExpressionChannel channel = new ExpressionChannel(fileName, reader);

        while (!channel.done()) {}

        Expression expression = channel.getExpression();

        removeSpace();

        return new ObjectExpression(expression);
    }

    private Object parseObjectArray() {
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
        Object object = parseObject();
        while (null != object) {
            objects.add(object);
            object = parseObject();
        }

        removeSpaceOrCR();

        Token rb = reader.nextToken();
        if (rb.getKind() != Kind.RB) {
            panicToken("must be }.", rb);
        }

        removeSpace();
        return new ObjectArray(id, ls, number, rs, ls, objects, rb);
    }

    private Object parseObjectStruct() {
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
        return new ObjectStruct(statements);
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

    private void panicNoMoreContent() {
        panicToken("no more content. check file '" + fileName + "' please.", reader.thisToken());
    }

    private void panicToken(String tip, Token token) {
        panic(tip, fileName, token);
    }

}
