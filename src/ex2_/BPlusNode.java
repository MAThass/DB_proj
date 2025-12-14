package ex2_;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

class BPlusNode {
    int order;
    List<Integer> keys;
    List<Integer> childAddresses;
    List<Record> records;
    int nextAddress;
    int parentAddress;
    boolean isLeaf;
    int pageAddress;

    public BPlusNode(int order) {
        this.order = order;
        this.keys = new ArrayList<>();
        this.childAddresses = new ArrayList<>();
        this.records = new ArrayList<>();
        this.nextAddress = -1;
        this.parentAddress = -1;
        this.isLeaf = false;
        this.pageAddress = -1;
    }

    public byte[] serialize() {
        int numKeys = keys.size();
        int headerSize = 6 * Integer.SIZE/8;
        int keysSize = numKeys * Integer.SIZE/8;
        int childSize = isLeaf ? 0 : (numKeys + 1) * Integer.SIZE/8;
        int recordsSize = isLeaf ? numKeys * Record.getSerializedSize() : 0;

        int totalSize = headerSize + keysSize + childSize + recordsSize;
        if (totalSize > ConstValues.PAGE_SIZE) {
            throw new RuntimeException("serialize error!!!!!! page size: " + totalSize);
        }

        ByteBuffer buffer = ByteBuffer.allocate(ConstValues.PAGE_SIZE);
        buffer.putInt(order);
        buffer.putInt(numKeys);
        buffer.putInt(isLeaf ? 1 : 0);
        buffer.putInt(nextAddress);
        buffer.putInt(parentAddress);
        buffer.putInt(pageAddress);
        for (int key : keys) {
            buffer.putInt(key);
        }
        if (!isLeaf) {
            for (int childAddr : childAddresses) {
                buffer.putInt(childAddr);
            }
        } else{
            for (Record record : records) {
                buffer.put(record.serialize());
            }
        }
        return buffer.array();
    }

    public static BPlusNode deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int order = buffer.getInt();
        int numKeys = buffer.getInt();
        boolean isLeaf = buffer.getInt() == 1;
        int nextAddress = buffer.getInt();
        int parentAddress = buffer.getInt();
        int pageAddress = buffer.getInt();

        BPlusNode node = new BPlusNode(order);
        node.isLeaf = isLeaf;
        node.nextAddress = nextAddress;
        node.parentAddress = parentAddress;
        node.pageAddress = pageAddress;

        for (int i = 0; i < numKeys; i++) {
            node.keys.add(buffer.getInt());
        }

        if (!isLeaf) {
            int numChildren = numKeys + 1;
            for (int i = 0; i < numChildren; i++) {
                node.childAddresses.add(buffer.getInt());
            }
        }else {
            for (int i = 0; i < numKeys; i++) {
                byte[] recordData = new byte[Record.getSerializedSize()];
                buffer.get(recordData);
                node.records.add(Record.deserialize(recordData));
            }
        }

        return node;
    }
}