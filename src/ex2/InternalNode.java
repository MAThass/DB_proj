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
    public int searchNode(int key){
        for( int i = 0; i < this.numberOfKeys; i++ ) {
            //childrenAddresses[numberOfKeys];

            if(key <= keys[i]){
                return childrenAddresses[i];
            }
        }
        return childrenAddresses[this.numberOfKeys + 1];
    }

    @Override
    public String displayContent() {
        return "";
    }
}
