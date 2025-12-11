package ex2;

import java.io.IOException;
import java.nio.ByteBuffer;

abstract public class NodePage {

    protected  int pageAddress;
    protected  int parentAddress;
    protected  int numberOfKeys = 0;
    protected boolean isLeaf;

    protected HandleIO handleIO;

    public int getPageAddress() {return pageAddress;}
    public int getParentAddress() {return parentAddress;}
    public int getNumberOfKeys() {return numberOfKeys;}

    public NodePage(HandleIO handleIO, int parentAddress, boolean isLeaf) throws IOException {
        this.handleIO = handleIO;
        this.pageAddress = handleIO.allocatePageAddress();
        this.parentAddress = parentAddress;
        this.isLeaf = isLeaf;
        this.numberOfKeys = 0;
    }

    public NodePage(HandleIO handleIO, int pageAddress, int parentAddress) throws IOException {
        this.handleIO = handleIO;
        this.pageAddress = handleIO.allocatePageAddress();
        this.parentAddress = parentAddress;
        this.pageAddress = pageAddress;
        this.numberOfKeys = 0;
    }

    public NodePage(HandleIO handleIO, int pageAddress, ByteBuffer buffer) throws IOException {
        this.handleIO = handleIO;
        this.pageAddress = pageAddress;
        deserialize(buffer);
    }

    public void deserialize(ByteBuffer buffer) throws IOException {
        this.isLeaf = buffer.getInt() == 1;
        //this.pageAddress = buffer.getInt();
        this.parentAddress = buffer.getInt();
        this.numberOfKeys = buffer.getInt();
    }

    public void serialize(ByteBuffer buffer) throws IOException {
        buffer.putInt(this.isLeaf ? 1 : 0);
        //buffer.putInt(this.pageAddress);
        buffer.putInt(this.parentAddress);
        buffer.putInt(this.numberOfKeys);

    }

    public abstract void writeToDisk() throws IOException;
    //public abstract void insert(Record record) throws IOException;

    public abstract NodePage split() throws IOException;

    public abstract Record search(int key);
    public abstract int searchNode(int key);

    public abstract String displayContent();
}
