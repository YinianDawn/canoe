package canoe.compiler;

import canoe.ast.AST;
import canoe.lexis.Kind;
import canoe.lexis.Lexer;
import canoe.lexis.Tokens;
import canoe.parser.Parser;

import java.util.LinkedList;
import java.util.List;

/**
 * @author dawn
 */
public class Compiler {

    private Options options;

    public Compiler(String[] args) {
        options = Options.parse(args);
    }


    public void compile() {
        build(options.sourceFiles, options);
    }

    private void build(List<SourceFile> sourceFiles, Options options) {
        for (SourceFile sourceFile : sourceFiles) {
            compile(sourceFile, options);
//            if (src.isCflatSource()) {
//                String destPath = opts.asmFileNameOf(src);
//                compile(src.path(), destPath, opts);
//                src.setCurrentName(destPath);
//            }
//            if (! opts.isAssembleRequired()) continue;
//            if (src.isAssemblySource()) {
//                String destPath = opts.objFileNameOf(src);
//                assemble(src.path(), destPath, opts);
//                src.setCurrentName(destPath);
//            }
        }
//        if (! opts.isLinkRequired()) return;
//        link(opts);
    }

    private static void compile(SourceFile sourceFile, Options options) {
        Tokens tokens = Lexer.parseTokens(sourceFile);
//        printTokens(tokens);
        AST ast = Parser.parseAst(tokens);
        printAST(ast);

    }
    private static void printAST(AST ast) {
        System.out.println("----------------------> AST: package:" + ast.getPackageName() + " file:" + ast.getFileName());

        System.out.println("print other things");
    }

    public static void printTokens(Tokens tokens) {
        System.out.println("----------------------> Tokens: " + tokens.getFileName());
        List<String> tips = new LinkedList<>();
        tokens.getTokens().forEach(t -> {
            if (t.getKind() == Kind.CR) {
                tips.forEach(System.out::println);
                tips.clear();
                System.err.println(String.format("[%d:%d:%d] CR \\n", t.getLine(), t.getIndex(), t.getLength()));
            } else {
                tips.add(String.format("[%d:%d:%d] %s %s", t.getLine(), t.getIndex(), t.getLength(), t.getKind().name(), t.getValue()));
            }
        });
        tips.forEach(System.out::println);
    }

    private static void sleep() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
