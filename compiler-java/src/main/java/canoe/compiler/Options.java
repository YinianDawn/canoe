package canoe.compiler;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static canoe.util.Util.panic;

/**
 * @author dawn
 */

public class Options {

    private static final String MINUS = "-";

    List<SourceFile> sourceFiles;


    static Options parse(String[] args) {
        Options opts = new Options();
        opts.parseArgs(args);
        return opts;
    }

    private void parseArgs(String[] args) {
        boolean end = false;
        List<String> fileNames = new LinkedList<>();
        for (String arg : args) {
            if ("--".equals(arg)) {
                end = true;
                continue;
            }
            if (!end && parseArg(arg)) {
                continue;
            }
            fileNames.add(arg);
        }

        sourceFiles = fileNames.stream().distinct().map(SourceFile::new).collect(Collectors.toList());

        if (sourceFiles.isEmpty()) {
            panic("no input file");
        }

    }

    private boolean parseArg(String arg) {
        if (arg.startsWith(MINUS)) {
            System.out.println("arg: " + arg);
            panic("what is arg: " + arg);
            return true;
        }
        return false;
    }


}
