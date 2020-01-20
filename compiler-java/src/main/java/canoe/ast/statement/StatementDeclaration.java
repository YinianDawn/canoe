package canoe.ast.statement;

import canoe.ast.expression.Expression;
import canoe.ast.expression.ExpressionParams;
import canoe.ast.statement.desc.*;
import canoe.lexis.Token;

import java.util.List;

/**
 * @author dawn
 */
public class StatementDeclaration implements Statement {

    private Expression expressionId;

    private DescOpen descOpen;

    private DescNative descNative;

    private DescGoto descGoto;

    private DescEnum descEnum;

    private List<DescId> descIds;

    private DescStatements descStatements;

    private DescFunction descFunction;

    private Token assign;

    private ExpressionParams bodyParam;

    private Object body;

    public StatementDeclaration(Expression expressionId, DescOpen descOpen, DescNative descNative, DescGoto descGoto, DescEnum descEnum, List<DescId> descIds, DescStatements descStatements, DescFunction descFunction, Token assign, ExpressionParams bodyParam, Object body) {
        this.expressionId = expressionId;
        this.descOpen = descOpen;
        this.descNative = descNative;
        this.descGoto = descGoto;
        this.descEnum = descEnum;
        this.descIds = descIds;
        this.descStatements = descStatements;
        this.descFunction = descFunction;
        this.assign = assign;
        this.bodyParam = bodyParam;
        this.body = body;
    }
}
