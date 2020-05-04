package canoe.compiler;


import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static canoe.util.PanicUtil.panic;


/**
 * @author dawn
 */
class Option {

    private static final String MINUS = "-";
    private static final String MINUS_MINUS = "--";

    private List<SourceFile> sourceFiles;

    static Option parse(String[] args) {
        Option option = new Option();
        option.parseArgs(args);
        return option;
    }

    public List<SourceFile> getSourceFiles() {
        return sourceFiles;
    }

    private void parseArgs(String[] args) {
        boolean end = false;
        List<String> names = new LinkedList<>();
        for (String arg : args) {
            // 单独 -- 后面的参数都不是配置了
            if (MINUS_MINUS.equals(arg)) { end = true; continue; }
            if (!end && parseArg(arg)) { continue; }
            names.add(arg);
        }

        sourceFiles = names.stream().distinct()
                .map(SourceFile::new).collect(Collectors.toList());

        if (sourceFiles.isEmpty()) {
            panic("no input file");
        }

    }

    private boolean parseArg(String arg) {
        if (!arg.startsWith(MINUS)) { return false; }

        // - 或 -- 开头
        System.out.println("arg: " + arg);
        panic("what is arg: " + arg);
        return true;
    }

}
