package ex2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class HandleIO {
    private String fileName = "file.dat";
    private int offset = 0;

    private RandomAccessFile file;

    public HandleIO() throws IOException {}
    public HandleIO(String fileName) throws IOException {
        this.fileName = fileName;
    }

    public void open() throws IOException {

        file = new RandomAccessFile(fileName, "rw");
    }

    public void close() throws IOException {
        file.close();
    }

    public void readPage(int pageAddress, byte[] buffer) throws IOException{
        offset = pageAddress * ConstValues.pageSize;
        file.seek(offset);
        file.read(buffer);
        Statistic.incrementReadBlocksCounter();
    }

    public void writePage(int pageAddress, byte[] buffer) throws IOException{
        file.seek(offset);
        file.write(buffer);
        //file.write(buffer, offset, ConstValues.pageSize);
        Statistic.incrementWriteBlocksCounter();
    }

    public int allocatePage() throws IOException{
        return (int)(file.length() / ConstValues.pageSize);
    }
}
