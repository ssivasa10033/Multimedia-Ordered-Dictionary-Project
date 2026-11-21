/**
 * A single piece of multimedia content (text, audio, image or html)
 * stored in the list of a dictionary entry.
 *
 * @author Seon Sivasathan
 */
public class MultimediaItem {

    // Descriptor of the content: text, or the name of an audio/image/html file
    private String content;

    // 1 = text, 2 = audio file, 3 = image file, 4 = html document
    private int type;

    /**
     * Creates a new MultimediaItem with the given content and type.
     */
    public MultimediaItem(String newContent, int newType) {
        content = newContent;
        type = newType;
    }

    /** Returns the content of this item. */
    public String getContent() {
        return content;
    }

    /** Returns the type of this item. */
    public int getType() {
        return type;
    }
}
