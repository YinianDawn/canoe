package canoe.compiler;

import canoe.lexis.Kind;
import canoe.lexis.Lexer;
import canoe.lexis.Tokens;

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
        tokens.getTokens().forEach(t -> {
            if (t.getKind() == Kind.CR) {
                System.err.println("CR l:" + t.getLine() + " i:" + t.getIndex() + " l:" + t.getLine() + " v:\\n ");
                sleep();
            } else {
                System.out.println(t);
                sleep();
            }
        });
    }


    private static void sleep() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
