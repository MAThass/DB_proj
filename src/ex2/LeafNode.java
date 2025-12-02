package ex2;


import java.io.IOException;
import java.nio.ByteBuffer;

public class LeafNode extends NodePage {

    private Record[] records = new Record[ConstValues.MAX_LEAF_KEYS];
    private int previousLeafAddress = -1;
    private int nextLeafAddress = -1;

    public LeafNode(HandleIO handleIO, int pageAddress, ByteBuffer buffer) throws IOException {
        super(handleIO, pageAddress, buffer);
    }

    public LeafNode(HandleIO handleIO, int parentAddress, boolean isLeaf) throws IOException {
        super(handleIO, parentAddress, isLeaf);
        //writeToDisk();
    }

    @Override
    protected void deserialize(ByteBuffer buffer) throws IOException {
        for(int i = 0; i < this.numberOfKeys; i++) {
            records[i] = Record.deserialize(buffer);
        }
    }

    @Override
    public void serialize() throws IOException {

    }

    @Override
    public NodePage insert(Record record) throws IOException {
        return null;
    }

    @Override
    public NodePage split() throws IOException {
        return null;
    }

    @Override
    public Record search(int key) {
        return null;
    }

    @Override
    public String displayContent() {
        return "";
    }
}
