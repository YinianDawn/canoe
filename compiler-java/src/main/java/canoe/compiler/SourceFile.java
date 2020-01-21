package canoe.compiler;

import static canoe.util.PanicUtil.panic;

/**
 * @author dawn
 */
public class SourceFile {

    private final static String EXT_SOURCE = ".canoe";

    private final String name;

    SourceFile(String name) {
        checkExt(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private static void checkExt(String name) {
        int index = name.lastIndexOf(".");
        if (-1 == index) {
            panic("wrong source file: " + name);
        }
        String ext = name.substring(index);
        if (!EXT_SOURCE.equals(ext)) {
            panic("wrong source file: " + name);
        }
    }

}
