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
       // writeToDisk();
    }

    public NodePage(HandleIO handleIO, int pageAddress, ByteBuffer buffer) throws IOException {
        this.handleIO = handleIO;
        this.pageAddress = pageAddress;
        deserializeHeader(buffer);
    }

    private void deserializeHeader(ByteBuffer buffer) throws IOException {
        this.pageAddress = buffer.getInt();
        this.parentAddress = buffer.getInt();
        this.numberOfKeys = buffer.getInt();
        this.isLeaf = buffer.get() == 1;
    }

    public abstract void writeToDisk() throws IOException;

    public abstract NodePage insert(Record record) throws IOException;

    public abstract NodePage split() throws IOException;

    public abstract Record search(int key);

    public abstract String displayContent();
}
