package ex2;


import java.io.IOException;
import java.nio.ByteBuffer;

public class LeafNode extends NodePage {

    private Record[] records = new Record[ConstValues.MAX_RECORDS];
    private int previousLeafAddress = -1;
    private int nextLeafAddress = -1;

    public LeafNode(HandleIO handleIO, int pageAddress, ByteBuffer buffer) throws IOException {
        super(handleIO, pageAddress, buffer);
    }

    public LeafNode(HandleIO handleIO, int parentAddress, boolean isLeaf) throws IOException {
        super(handleIO, parentAddress, isLeaf);
        writeToDisk();
    }

    @Override
    public void writeToDisk() throws IOException {

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
