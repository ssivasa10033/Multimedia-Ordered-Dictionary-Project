/**
 * Thrown when a multimedia item cannot be played or displayed, for example
 * because its file is missing or is not in a supported format.
 *
 * Support type, written to make this project self-contained.
 */
public class MultimediaException extends Exception {

    private static final long serialVersionUID = 1L;

    public MultimediaException() {
        super("Multimedia Error");
    }

    public MultimediaException(String message) {
        super(message);
    }
}
