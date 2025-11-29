package ex2;


import java.io.IOException;
import java.nio.ByteBuffer;

public class LeafNode extends NodePage {

    Record[] records;

    public LeafNode(HandleIO handleIO, int parentAddress, ByteBuffer buffer) throws IOException {
        super(handleIO, parentAddress);
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
