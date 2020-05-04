package canoe.util;

import canoe.ast.Ast;
import canoe.lexer.Tokens;
import canoe.parser.syntax.ConcreteSyntax;

/**
 * @author dawn
 */
public class PrintUtil {

    public static void print(Ast ast) {
        print("==============> Ast: " + ast.getSyntax().getTokens().getSourceFile().getName());
        print("package: " + ast.getPackageName().value());
        ast.getImports().forEach(u -> print("import: " + u, true));
        ast.getStms().forEach((id, s) -> print(id + ": " + s.toString()));
        System.out.println();
    }

    public static void print(ConcreteSyntax syntax) {
        print("==============> Syntax: " + syntax.getTokens().getSourceFile().getName());
        syntax.getPackageInfo().dump(PrintUtil::print);
        syntax.getImportInfo().dump(s -> print(s, true));
        syntax.getStatements().dump(PrintUtil::print);
        System.out.println();
    }

    public static void print(Tokens tokens) {
        print("==============> Tokens: " + tokens.getSourceFile().getName());
        tokens.getTokens().forEach(t -> {
            if (t.isCR()) {
                print(String.format("[%d:%d:%d] CR <\\n>", t.line, t.position, t.size), true);
            } else {
                print(String.format("[%d:%d:%d] %s %s", t.line, t.position, t.size, t.kind.name(), t.value()));
            }
        });
        System.out.println();
    }

    static void print(String info) {
        print(info, false);
    }

    static void print(String info, boolean error) {
        if (error) {
            System.out.format("\033[31;0m%s\033[0m%n", info);
        } else {
            System.out.format("%s%n", info);
        }
    }

}
