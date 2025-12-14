package ex2_;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

// Node class for B+ Tree
class BPlusNode {
    int order;
    List<Integer> keys;
    List<Integer> childAddresses;  // Page addresses instead of object references
    List<Record> records;
    int nextAddress;               // Address of next leaf (-1 if none)
    int parentAddress;             // Address of parent (-1 if root)
    boolean isLeaf;
    int pageAddress;               // This node's page address

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

    /**
     * Serialize node to byte array
     *
     * Format:
     * [4 bytes: order]
     * [4 bytes: number of keys]
     * [4 bytes: isLeaf (1=true, 0=false)]
     * [4 bytes: nextAddress]
     * [4 bytes: parentAddress]
     * [4 bytes: pageAddress]
     * [keys array: numKeys * 4 bytes]
     * [childAddresses array: (numKeys+1) * 4 bytes] (only for internal nodes)
     * [records array: numKeys * 20 bytes] (only for leaf nodes)
     */
    public byte[] serialize() {
        // Calculate required size
        int numKeys = keys.size();
        int headerSize = 24; // 6 * 4 bytes
        int keysSize = numKeys * 4;
        int childSize = isLeaf ? 0 : (numKeys + 1) * 4;
        int recordsSize = isLeaf ? numKeys * Record.getSerializedSize() : 0;

        int totalSize = headerSize + keysSize + childSize + recordsSize;

        if (totalSize > ConstValues.PAGE_SIZE) {
            throw new RuntimeException("Node too large for page: " + totalSize + " bytes");
        }

        ByteBuffer buffer = ByteBuffer.allocate(ConstValues.PAGE_SIZE);

        // Header
        buffer.putInt(order);
        buffer.putInt(numKeys);
        buffer.putInt(isLeaf ? 1 : 0);
        buffer.putInt(nextAddress);
        buffer.putInt(parentAddress);
        buffer.putInt(pageAddress);

        // Keys
        for (int key : keys) {
            buffer.putInt(key);
        }

        // Child addresses (internal nodes only)
        if (!isLeaf) {
            for (int childAddr : childAddresses) {
                buffer.putInt(childAddr);
            }
        }

        // Records (leaf nodes only)
        if (isLeaf) {
            for (Record record : records) {
                buffer.put(record.serialize());
            }
        }

        return buffer.array();
    }

    /**
     * Deserialize node from byte array
     */
    public static BPlusNode deserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        // Read header
        int order = buffer.getInt();
        int numKeys = buffer.getInt();
        boolean isLeaf = buffer.getInt() == 1;
        int nextAddress = buffer.getInt();
        int parentAddress = buffer.getInt();
        int pageAddress = buffer.getInt();

        // Create node
        BPlusNode node = new BPlusNode(order);
        node.isLeaf = isLeaf;
        node.nextAddress = nextAddress;
        node.parentAddress = parentAddress;
        node.pageAddress = pageAddress;

        // Read keys
        for (int i = 0; i < numKeys; i++) {
            node.keys.add(buffer.getInt());
        }

        // Read child addresses (internal nodes)
        if (!isLeaf) {
            int numChildren = numKeys + 1;
            for (int i = 0; i < numChildren; i++) {
                node.childAddresses.add(buffer.getInt());
            }
        }

        // Read records (leaf nodes)
        if (isLeaf) {
            for (int i = 0; i < numKeys; i++) {
                byte[] recordData = new byte[Record.getSerializedSize()];
                buffer.get(recordData);
                node.records.add(Record.deserialize(recordData, 0));
            }
        }

        return node;
    }

    /**
     * Calculate maximum number of keys that fit in a page
     */
    public static int calculateMaxKeys(int order, boolean isLeaf) {
        int headerSize = 24;
        int availableSpace = ConstValues.PAGE_SIZE - headerSize;

        if (isLeaf) {
            // Each entry: 4 bytes (key) + 20 bytes (record)
            int entrySize = 4 + Record.getSerializedSize();
            return availableSpace / entrySize;
        } else {
            // Each entry: 4 bytes (key) + 4 bytes (child pointer)
            // Plus one extra child pointer
            int entrySize = 4 + 4;
            return (availableSpace - 4) / entrySize;
        }
    }
}