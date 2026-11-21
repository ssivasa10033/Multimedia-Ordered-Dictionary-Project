/**
 * Thrown when a dictionary operation refers to a key that is not stored.
 *
 * Support type, written to make this project self-contained.
 */
public class DictionaryException extends Exception {

    private static final long serialVersionUID = 1L;

    public DictionaryException() {
        super("Dictionary Error");
    }

    public DictionaryException(String message) {
        super(message);
    }
}
