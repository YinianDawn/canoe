package canoe.util;

import canoe.lexer.Tokens;

/**
 * @author dawn
 */
public class PrintUtil {

//    public static void print(AST ast) {
//        print("==============> AST: " + ast.getTokens().getSourceFile().getName());
//        tokens.getTokens().forEach(t -> {
//            if (t.isCR()) {
//                print(String.format("[%d:%d:%d] CR <\\n>", t.line, t.position, t.size), true);
//            } else {
//                print(String.format("[%d:%d:%d] %s %s", t.line, t.position, t.size, t.kind.name(), t.getValue()));
//            }
//        });
//    }

    public static void print(Tokens tokens) {
        print("==============> Tokens: " + tokens.getSourceFile().getName());
        tokens.getTokens().forEach(t -> {
            if (t.isCR()) {
                print(String.format("[%d:%d:%d] CR <\\n>", t.line, t.position, t.size), true);
            } else {
                print(String.format("[%d:%d:%d] %s %s", t.line, t.position, t.size, t.kind.name(), t.value()));
            }
        });
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
