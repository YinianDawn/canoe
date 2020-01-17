package canoe.compiler;

import java.io.File;

import static canoe.util.Util.panic;

/**
 * @author dawn
 */
public class SourceFile {

    private final static String EXT_SOURCE = ".canoe";

    private final String fileName;

    SourceFile(String fileName) {
        checkExt(fileName);
        this.fileName = fileName;
    }

    public File getFile() {
        return new File(fileName);
    }

    public String getFileName() {
        return fileName;
    }

    private static void checkExt(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (-1 == index) {
            panic("wrong source file: " + fileName);
        }
        String ext = fileName.substring(index);
        if (!EXT_SOURCE.equals(ext)) {
            panic("wrong source file: " + fileName);
        }
    }
}
