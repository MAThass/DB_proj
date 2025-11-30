package ex2;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BPlusTree {
    private HandleIO handleIO;
    private int rootAdrress;
    private double alpha;
    private int treeDegree = ConstValues.treeDegree;

    public BPlusTree(String filename) throws IOException {
        handleIO = new HandleIO(filename);
        handleIO.open();
        rootAdrress = handleIO.allocatePageAddress();
    }

    public BPlusTree(String filename, int treeDegree ) throws IOException {
        handleIO = new HandleIO(filename);
        handleIO.open();
        rootAdrress = handleIO.allocatePageAddress();
        this.treeDegree = treeDegree;
    }

    public void insertRecord(Record record) throws IOException {

    }

    public Record getRecord(int key) throws IOException {
        return null;
    }

    public void traverseTree() throws IOException {

    }

    private NodePage loadNode(int pageAddress) throws IOException {
        byte[] data = new byte[ConstValues.pageSize];
        handleIO.readPage(pageAddress, data);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.position(0);
        buffer.getInt();
        buffer.getInt();
        buffer.getInt();
        boolean isLeaf = (buffer.get() == 1);
        buffer.position(0);
        if (isLeaf) {
            return new LeafNode(handleIO, pageAddress, buffer);
        }
        return new InternalNode(handleIO, pageAddress, buffer);
    }

    private LeafNode findLeafNode(int key) throws IOException {
        return null;
    }

    private void handleRootSplit(NodePage oldRoot, NodePage newRoot, int promotedKey) throws IOException {

    }

    private void printTree() throws IOException {

    }
}
