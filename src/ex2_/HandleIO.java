package ex2_;

import ex2.ConstValues;
import ex2.Statistic;

import java.io.IOException;
import java.io.RandomAccessFile;

public class HandleIO {
    private String fileName;

    private RandomAccessFile file;
    private boolean isOpen = false;

    public HandleIO() throws IOException {
        fileName = "file.dat";
    }
    public HandleIO(String fileName) throws IOException {
        this.fileName = fileName;
    }

    public void open() throws IOException {
        if (!isOpen) {
            file = new RandomAccessFile(fileName, "rw");
            isOpen = true;
        }
    }

    public void close() throws IOException {
        if (isOpen) {
            file.close();
            isOpen = false;
        }
    }

    public void readPage(int pageAddress, byte[] buffer) throws IOException{
        long offset = (long) pageAddress * ConstValues.PAGE_SIZE;
        file.seek(offset);
        int bytesRead = file.read(buffer);
        if (bytesRead != ConstValues.PAGE_SIZE) {
            throw new IOException("Incomplete page read: " + bytesRead + " bytes");
        }
        Statistic.incrementReadBlocksCounter();
    }

    public void writePage(int pageAddress, byte[] buffer) throws IOException{
        long offset = (long) pageAddress * ConstValues.PAGE_SIZE;
        file.seek(offset);
        file.write(buffer);
        //file.write(buffer, offset, ConstValues.pageSize);
        Statistic.incrementWriteBlocksCounter();
    }

    public int allocatePageAddress() throws IOException{
        return (int)(file.length() / ConstValues.PAGE_SIZE);
//        byte[] emptyPage = new byte[ConstValues.PAGE_SIZE];
//        int newAddress = (int)(file.length() / ConstValues.PAGE_SIZE);
//        writePage(newAddress, emptyPage);
//        return newAddress;
    }
}
