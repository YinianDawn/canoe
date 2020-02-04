package canoe.util;

import java.util.function.Consumer;

/**
 * @author dawn
 */
public interface Dump {

    /**
     * 输出
     * @param print
     */
    void dump(Consumer<String> print);

}
