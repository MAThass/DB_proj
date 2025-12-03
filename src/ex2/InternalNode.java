package ex2;

import java.io.IOException;
import java.nio.ByteBuffer;

public class InternalNode extends NodePage{

    int[] childrenAddresses = new int[ConstValues.MAX_INTERNAL_KEYS + 1];
    int[] keys = new int[ConstValues.MAX_INTERNAL_KEYS];

    public InternalNode(HandleIO handleIO, int pageAddress, ByteBuffer buffer) throws IOException {
        super(handleIO, pageAddress, buffer);
        deserialize(buffer);
    }

    // Konieczny konstruktor do tworzenia NOWEGO węzła wewnętrznego
    public InternalNode(HandleIO handleIO, int parentAddress, boolean isLeaf) throws IOException {
        super(handleIO, parentAddress, isLeaf);
        // Po super() węzeł ma już przydzielony nowy pageAddress
         // Zapisz pusty węzeł
    }

    @Override
    public void deserialize(ByteBuffer buffer) throws IOException{
        super.deserialize(buffer);
        for( int i = 0; i < this.numberOfKeys; i++ ) {
            childrenAddresses[numberOfKeys] = buffer.getInt();
            keys[numberOfKeys] = buffer.getInt();
        }
        childrenAddresses[numberOfKeys] = buffer.getInt();
    }

    @Override
    public void serialize(ByteBuffer buffer) throws IOException {
        super.serialize(buffer);
        for( int i = 0; i < this.numberOfKeys; i++ ) {
            buffer.putInt(childrenAddresses[numberOfKeys]);
            buffer.putInt(keys[numberOfKeys]);
        }
        buffer.putInt(childrenAddresses[numberOfKeys]);
    }

    @Override
    public void writeToDisk() throws IOException {
        byte[] data = new byte[ConstValues.PAGE_SIZE];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.serialize(buffer);
        handleIO.writePage(pageAddress, data);
    }

    @Override
    public void insert(Record record) throws IOException {

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
    public int searchNode(int key){
        int L = 0;
        int R = this.numberOfKeys - 1;
        int searchedIndex = this.numberOfKeys;
        while(L <= R) {
            int S = (L + R) / 2;
            if (keys[S] >= key) {
                searchedIndex = S;
                R = S - 1;
            } else {
                L = S + 1;
            }
        }
        return childrenAddresses[searchedIndex];
    }

    @Override
    public String displayContent() {
        return "";
    }
}
