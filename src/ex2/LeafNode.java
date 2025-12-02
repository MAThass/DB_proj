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
    public void deserialize(ByteBuffer buffer) throws IOException {
        super.deserialize(buffer);
        this.previousLeafAddress = buffer.getInt();
        this.nextLeafAddress = buffer.getInt();
        for(int i = 0; i < this.numberOfKeys; i++) {
            records[i] = Record.deserialize(buffer);
        }
    }

    @Override
    public void serialize(ByteBuffer buffer) throws IOException {
        super.serialize(buffer);
        buffer.putInt(this.previousLeafAddress);
        buffer.putInt(this.nextLeafAddress);
        for(int i = 0; i < this.numberOfKeys; i++) {
            records[i].serialize(buffer);
        }
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
        for(int i = 0; i < this.numberOfKeys; i++){
            if( key == records[i].getKey()){
                return records[i];
            }
        }
        return null;
    }

    @Override
    public int searchNode(int key){
        return -1;
    }

    @Override
    public String displayContent() {
        return "";
    }
}
