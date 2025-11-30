package ex2;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class InternalNode extends NodePage{

    int[] childrenAddresses = new int[ConstValues.MAX_KEYS + 1];
    int[] keys = new int[ConstValues.MAX_KEYS];

    public InternalNode(HandleIO handleIO, int pageAddress, ByteBuffer buffer) throws IOException {
        super(handleIO, pageAddress, buffer);
    }

    // Konieczny konstruktor do tworzenia NOWEGO węzła wewnętrznego
    public InternalNode(HandleIO handleIO, int parentAddress, boolean isLeaf) throws IOException {
        super(handleIO, parentAddress, isLeaf);
        // Po super() węzeł ma już przydzielony nowy pageAddress
        writeToDisk(); // Zapisz pusty węzeł
    }

    private void deserialize(ByteBuffer buffer) throws IOException {
        try {
            while (true) {
                childrenAddresses[numberOfKeys] = buffer.getInt();
                keys[numberOfKeys] = buffer.getInt();
                numberOfKeys++;
            }
        } catch (BufferUnderflowException e) {
            return;
        }
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
