import java.util.ArrayList;

/**
 * The data stored in an internal node of the binary search tree:
 * a key (name) and the multimedia items associated with that key.
 *
 * @author Seon Sivasathan
 */
public class Data {

    // Key attribute of this data item
    private String name;

    // Multimedia objects associated with the key
    private ArrayList<MultimediaItem> media;

    /**
     * Creates a Data object with the given key and an empty media list.
     */
    public Data(String newName) {
        name = newName;
        media = new ArrayList<MultimediaItem>();
    }

    /** Adds newItem to the media list of this object. */
    public void add(MultimediaItem newItem) {
        media.add(newItem);
    }

    /** Returns the key attribute of this object. */
    public String getName() {
        return name;
    }

    /** Returns the media list stored in this object. */
    public ArrayList<MultimediaItem> getMedia() {
        return media;
    }
}
