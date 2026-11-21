import java.util.ArrayList;

/**
 * Ordered dictionary implemented with a binary search tree. Only internal
 * nodes store data; every external position is a leaf BSTNode. All operations
 * work directly on the tree (no copying into other data structures) and run
 * in time proportional to the height of the tree.
 *
 * @author Seon Sivasathan
 */
public class BSTOrderedDictionary implements BSTOrderedDictionaryADT {

    private BSTNode root;
    private int numInternalNodes;

    /**
     * Creates an empty dictionary: the root is a leaf node.
     */
    public BSTOrderedDictionary() {
        root = new BSTNode();
        numInternalNodes = 0;
    }

    /** Returns the root of this tree. */
    public BSTNode getRoot() {
        return root;
    }

    /** Returns the number of internal nodes in this tree. */
    public int getNumInternalNodes() {
        return numInternalNodes;
    }

    /**
     * Returns the media list stored with the given key in the tree with
     * root r, or null if no node stores the key.
     */
    public ArrayList<MultimediaItem> get(BSTNode r, String key) {
        BSTNode node = findNode(r, key);
        if (node.isLeaf()) {
            return null;
        }
        return node.getData().getMedia();
    }

    /**
     * Adds a MultimediaItem(content, type) to the entry with the given key,
     * creating a new internal node if the key is not yet in the tree.
     */
    public void put(BSTNode r, String key, String content, int type) {
        BSTNode node = findNode(r, key);

        if (node.isLeaf()) {
            // Key not present: turn this leaf into an internal node
            node.setData(new Data(key));
            node.setLeftChild(new BSTNode(node, null, null, null));
            node.setRightChild(new BSTNode(node, null, null, null));
            numInternalNodes++;
        }
        node.getData().add(new MultimediaItem(content, type));
    }

    /**
     * Removes the node with the given key from the tree with root r.
     * Throws DictionaryException if the key is not in the tree.
     */
    public void remove(BSTNode r, String key) throws DictionaryException {
        BSTNode node = findNode(r, key);
        if (node.isLeaf()) {
            throw new DictionaryException("No record in the ordered dictionary has key " + key + ".");
        }
        removeInternalNode(node);
    }

    /**
     * Removes all MultimediaItems of the given type from the entry with the
     * given key; if its list becomes empty the node is removed from the tree.
     * Throws DictionaryException if the key is not in the tree.
     */
    public void remove(BSTNode r, String key, int type) throws DictionaryException {
        BSTNode node = findNode(r, key);
        if (node.isLeaf()) {
            throw new DictionaryException("No record in the ordered dictionary has key " + key + ".");
        }

        ArrayList<MultimediaItem> media = node.getData().getMedia();
        // Iterate backwards so removals do not disturb unvisited indices
        for (int i = media.size() - 1; i >= 0; i--) {
            if (media.get(i).getType() == type) {
                media.remove(i);
            }
        }

        if (media.isEmpty()) {
            removeInternalNode(node);
        }
    }

    /**
     * Returns the Data with the smallest key in the tree with root r,
     * or null if the tree is empty.
     */
    public Data smallest(BSTNode r) {
        if (r.isLeaf()) {
            return null;
        }
        BSTNode node = r;
        while (!node.getLeftChild().isLeaf()) {
            node = node.getLeftChild();
        }
        return node.getData();
    }

    /**
     * Returns the Data with the largest key in the tree with root r,
     * or null if the tree is empty.
     */
    public Data largest(BSTNode r) {
        if (r.isLeaf()) {
            return null;
        }
        BSTNode node = r;
        while (!node.getRightChild().isLeaf()) {
            node = node.getRightChild();
        }
        return node.getData();
    }

    /**
     * Returns the Data with the smallest key strictly greater than the given
     * key (the successor), or null if there is none. The key itself does not
     * need to be stored in the tree.
     */
    public Data successor(BSTNode r, String key) {
        BSTNode node = r;
        Data candidate = null;

        while (!node.isLeaf()) {
            if (key.compareTo(node.getData().getName()) < 0) {
                // This node is larger than key: remember it, look for a closer one
                candidate = node.getData();
                node = node.getLeftChild();
            } else {
                node = node.getRightChild();
            }
        }
        return candidate;
    }

    /**
     * Returns the Data with the largest key strictly smaller than the given
     * key (the predecessor), or null if there is none. The key itself does
     * not need to be stored in the tree.
     */
    public Data predecessor(BSTNode r, String key) {
        BSTNode node = r;
        Data candidate = null;

        while (!node.isLeaf()) {
            if (key.compareTo(node.getData().getName()) > 0) {
                // This node is smaller than key: remember it, look for a closer one
                candidate = node.getData();
                node = node.getRightChild();
            } else {
                node = node.getLeftChild();
            }
        }
        return candidate;
    }

    /**
     * Returns the internal node storing the key, or, if the key is not in
     * the tree, the leaf node where the key would be inserted.
     */
    private BSTNode findNode(BSTNode r, String key) {
        BSTNode node = r;

        while (!node.isLeaf()) {
            int comparison = key.compareTo(node.getData().getName());
            if (comparison == 0) {
                return node;
            } else if (comparison < 0) {
                node = node.getLeftChild();
            } else {
                node = node.getRightChild();
            }
        }
        return node;
    }

    /**
     * Removes the given internal node from the tree and updates the count.
     */
    private void removeInternalNode(BSTNode node) {
        BSTNode left = node.getLeftChild();
        BSTNode right = node.getRightChild();

        if (left.isLeaf()) {
            replaceNode(node, right);
        } else if (right.isLeaf()) {
            replaceNode(node, left);
        } else {
            // Two internal children: copy in the data of the in-order successor
            // (leftmost internal node of the right subtree) and unlink that node
            BSTNode successorNode = right;
            while (!successorNode.getLeftChild().isLeaf()) {
                successorNode = successorNode.getLeftChild();
            }
            node.setData(successorNode.getData());
            replaceNode(successorNode, successorNode.getRightChild());
        }
        numInternalNodes--;
    }

    /**
     * Puts replacement in the place of node in the tree (node is unlinked).
     */
    private void replaceNode(BSTNode node, BSTNode replacement) {
        BSTNode parent = node.getParent();

        replacement.setParent(parent);
        if (parent == null) {
            root = replacement;
        } else if (parent.getLeftChild() == node) {
            parent.setLeftChild(replacement);
        } else {
            parent.setRightChild(replacement);
        }
    }
}
