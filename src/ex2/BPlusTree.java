package ex2;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BPlusTree {
    private HandleIO handleIO;
    private int rootAdrress;
    private double alpha;
    private int treeDegree = ConstValues.TREE_DEGREE;

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
        LeafNode leafNode = findLeafNode(record.getKey());
        try{
            leafNode.insert(record);
        }catch(IOException e){
            handleLeafSplit(leafNode);
        }
    }

    // return null when do not find key
    public Record getRecord(int key) throws IOException {
        LeafNode leafNode = findLeafNode(key);
        if (leafNode == null) {
            return null;
        }
        return leafNode.search(key);
    }

    public void traverseTree() throws IOException {

    }

    private NodePage loadNode(int pageAddress) throws IOException {
        byte[] data = new byte[ConstValues.PAGE_SIZE];
        handleIO.readPage(pageAddress, data);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.position(0);
        boolean isLeaf = (buffer.getInt() == 1);
        buffer.position(0);
        if (isLeaf) {
            return new LeafNode(handleIO, pageAddress, buffer);
        }
        return new InternalNode(handleIO, pageAddress, buffer);
    }

    private LeafNode findLeafNode(int key) throws IOException {
        int currentAdrress = rootAdrress;
        NodePage currentNode = loadNode(currentAdrress);

        while (true){
            if(currentNode.isLeaf){
                return (LeafNode) currentNode;
            }
            currentAdrress = currentNode.searchNode(key);
            currentNode = loadNode(currentAdrress);
        }
        //return null;
    }

    private void handleRootSplit(NodePage oldRoot, NodePage newRoot, int promotedKey) throws IOException {

    }

    private void handleLeafSplit(LeafNode leafNode) throws IOException{
        //InternalNode node = (InternalNode) loadNode(nodeAddress);
        LeafNode newLeafNode = (LeafNode) leafNode.split();

    }

    private void handleInternalNode() throws IOException{

    }

    private void printTree() throws IOException {

    }
}
