/**
 * A node of the binary search tree. Internal nodes store a Data object and
 * have two children; leaf nodes have null data and null children.
 *
 * @author Seon Sivasathan
 */
public class BSTNode {

    private BSTNode parent;
    private BSTNode leftChild;
    private BSTNode rightChild;
    private Data data;

    /**
     * Creates a node whose instance variables are all null (a leaf node).
     */
    public BSTNode() {
        parent = null;
        leftChild = null;
        rightChild = null;
        data = null;
    }

    /**
     * Creates a node whose instance variables take the given values.
     */
    public BSTNode(BSTNode newParent, BSTNode newLeftChild,
                   BSTNode newRightChild, Data newData) {
        parent = newParent;
        leftChild = newLeftChild;
        rightChild = newRightChild;
        data = newData;
    }

    public BSTNode getParent() {
        return parent;
    }

    public BSTNode getLeftChild() {
        return leftChild;
    }

    public BSTNode getRightChild() {
        return rightChild;
    }

    public Data getData() {
        return data;
    }

    public void setParent(BSTNode newParent) {
        parent = newParent;
    }

    public void setLeftChild(BSTNode newLeftChild) {
        leftChild = newLeftChild;
    }

    public void setRightChild(BSTNode newRightChild) {
        rightChild = newRightChild;
    }

    public void setData(Data newData) {
        data = newData;
    }

    /**
     * Returns true if this node is a leaf (it has no children).
     */
    public boolean isLeaf() {
        return leftChild == null && rightChild == null;
    }
}
