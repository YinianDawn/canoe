package canoe.compiler;

import canoe.lexer.Lexer;
import canoe.lexer.Tokens;

import java.util.List;

/**
 * @author dawn
 */
public class Compiler {

    private Option option;

    public Compiler(String[] args) {
        option = Option.parse(args);
    }

    public void compile() {
        build(option.getSourceFiles());
    }

    private void build(List<SourceFile> sourceFiles) {
        for (SourceFile sourceFile : sourceFiles) {
            compile(sourceFile);
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

    private static void compile(SourceFile sourceFile) {
        Tokens tokens = Lexer.parseTokens(sourceFile);
        canoe.util.PrintUtil.print(tokens);
//        Syntax syntax = Parser.parseSyntax(tokens);
//        canoe.util.PrintUtil.print(syntax);

    }



}
