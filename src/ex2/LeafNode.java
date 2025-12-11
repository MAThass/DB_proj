package ex2;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class LeafNode extends NodePage {

    public Record[] records = new Record[ConstValues.MAX_LEAF_KEYS + 1]; // + 1 to handle overflow
    public int previousLeafAddress = -1;
    public int nextLeafAddress = -1;

    public LeafNode(HandleIO handleIO, int pageAddress, ByteBuffer buffer) throws IOException {
        super(handleIO, pageAddress, buffer);
        //deserialize(buffer);
    }

    public LeafNode(HandleIO handleIO, int parentAddress, boolean isLeaf) throws IOException {
        super(handleIO, parentAddress, isLeaf);
        this.previousLeafAddress = -1;
        this.nextLeafAddress = -1;
    }

    @Override
    public void deserialize(ByteBuffer buffer) throws IOException {
        super.deserialize(buffer);
        records = new Record[ConstValues.MAX_LEAF_KEYS + 1];
        this.previousLeafAddress = buffer.getInt();
        this.nextLeafAddress = buffer.getInt();
        for(int i = 0; i < this.numberOfKeys; i++) {
            records[i] = Record.deserialize(buffer);
            //records[i].deserialize(buffer);

        }
    }

    @Override
    public void writeToDisk() throws IOException {
        byte[] data = new byte[ConstValues.PAGE_SIZE];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.serialize(buffer);
        handleIO.writePage(pageAddress, data);
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

    //@Override
    public void insert(Record record) throws IOException {
        int insertIndex = numberOfKeys;
        for (int i = numberOfKeys - 1; i >= 0; i--) {
            if (records[i].getKey() > record.getKey()) {
                records[i + 1] = records[i];
                insertIndex = i;
            } else {
                break;
            }
        }

        records[insertIndex] = record;
        numberOfKeys++;
        if(numberOfKeys > ConstValues.MAX_LEAF_KEYS ) {
            throw new IOException("Overflow leaf");
        }
        this.writeToDisk();
    }

    @Override
    public NodePage split() throws IOException {
        int recordNumber = (ConstValues.MAX_LEAF_KEYS + 1) / 2;

        LeafNode newLeafNode = new LeafNode(handleIO, handleIO.allocatePageAddress(), true);

        newLeafNode.records = Arrays.copyOfRange(records, recordNumber, records.length); //0, recordNumber);
        newLeafNode.numberOfKeys = recordNumber;
        records = Arrays.copyOf(records, recordNumber);
        numberOfKeys = records.length;

        int oldNextAddress = this.nextLeafAddress;
        this.nextLeafAddress = newLeafNode.pageAddress;
        newLeafNode.previousLeafAddress = this.pageAddress;
        newLeafNode.nextLeafAddress = oldNextAddress;
        if (oldNextAddress != -1) {}

        this.writeToDisk();

        newLeafNode.writeToDisk();

        return newLeafNode;
    }

    @Override
    public Record search(int key) {
        int L = 0;
        int R = this.numberOfKeys - 1;
        int S ;
        while(L <= R){
            S = ( L + R ) / 2;
            if(records[S].getKey() < key){
                L = S + 1;
            }
            else if(records[S].getKey() > key){
                R = S - 1;
            }else {
                return records[S];
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
