package canoe.ast;

import canoe.ast.scope.Scope;
import canoe.ast.stm.Stm;
import canoe.ast.stm.StmUtil;
import canoe.lexer.Token;
import canoe.parser.syntax.ConcreteSyntax;
import canoe.parser.syntax.imports.ImportExclude;
import canoe.parser.syntax.imports.ImportInclude;
import canoe.parser.syntax.imports.ImportUnit;
import canoe.util.PanicUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

/**
 * @author dawn
 */
public class Ast {

    private ConcreteSyntax syntax;

    public static Ast parseAST(ConcreteSyntax syntax) {
        return new Ast(syntax);
    }

    private Ast(ConcreteSyntax syntax) {
        this.syntax = syntax;
        init();
    }

    private static HashMap<String, PathAst> parsed = new HashMap<>();
    private static Stack<String> parsing = new Stack<>();

    private Token packageName;
    private List<ImportUnit> imports;
    private LinkedHashMap<String, Stm> stms = new LinkedHashMap<>();
    private Scope scope = new Scope(null, 1);

    private void init() {
        packageName = syntax.getPackageInfo().getName();
        imports = syntax.getImportInfo().get();
        int[] count = new int[]{0};
        syntax.getStatements().getStatements().forEach(s -> {
            String id = s.getId();
            if (null == id) {
                count[0]++;
                id = String.format("#%d", count[0]);
            }
            stms.put(id, StmUtil.parse(s));
        });
    }

    public void analysis() {
        analysisImports();
        PanicUtil.panic("");
    }

    private void analysisImports() {
        for (ImportUnit u : imports) {
            Token id = u.getId();
            Token path = u.getId();
            ImportInclude include = u.getInclude();
            ImportExclude exclude = u.getExclude();
            analysisImport(id, path, include, exclude);
        }
    }
    private void analysisImport(Token id, Token path, ImportInclude include, ImportExclude exclude) {
        String p = path.value();
        PathAst pathAst = parsed.get(p);
        if (null == pathAst) {
            pathAst = parsePath(p);
        }

//        Ast ast = getAst()
        System.out.println();
    }

    private PathAst parsePath(String path) {




        return null;
    }

    public Token getPackageName() {
        return packageName;
    }

    public List<ImportUnit> getImports() {
        return imports;
    }

    public LinkedHashMap<String, Stm> getStms() {
        return stms;
    }

    public ConcreteSyntax getSyntax() {
        return syntax;
    }
}
