import java.util.ArrayList;

/**
 * Interface for an ordered dictionary storing multimedia items under String
 * keys. Every operation takes the root of the (sub)tree to operate on, so the
 * same methods serve both the whole dictionary and any subtree of it.
 *
 * Support type, written to make this project self-contained.
 */
public interface BSTOrderedDictionaryADT {

    /** Returns the root node of the tree. */
    public BSTNode getRoot();

    /** Returns the number of internal (data-bearing) nodes in the tree. */
    public int getNumInternalNodes();

    /** Returns the items stored under key, or null if key is absent. */
    public ArrayList<MultimediaItem> get(BSTNode r, String key);

    /** Adds a MultimediaItem(content, type) to the entry for key. */
    public void put(BSTNode r, String key, String content, int type);

    /** Removes the entry for key. Throws if key is absent. */
    public void remove(BSTNode r, String key) throws DictionaryException;

    /** Removes every item of the given type from the entry for key. */
    public void remove(BSTNode r, String key, int type) throws DictionaryException;

    /** Returns the entry with the smallest key, or null if empty. */
    public Data smallest(BSTNode r);

    /** Returns the entry with the largest key, or null if empty. */
    public Data largest(BSTNode r);

    /** Returns the entry with the smallest key greater than key, else null. */
    public Data successor(BSTNode r, String key);

    /** Returns the entry with the largest key smaller than key, else null. */
    public Data predecessor(BSTNode r, String key);
}
