package canoe.parser.syntax;

/**
 * @author dawn
 */
public interface Produce<T> {

    /**
     * 检查
     * @param file
     * @return
     */
    T make(String file);

}
