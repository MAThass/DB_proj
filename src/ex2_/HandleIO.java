package ex2_;

import java.io.File;
import java.io.FileWriter;
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
        readPage(pageAddress, buffer, false);
    }

    public void readPage(int pageAddress, byte[] buffer, boolean printing) throws IOException{
        long offset = (long) pageAddress * ConstValues.PAGE_SIZE;
        file.seek(offset);
        int bytesRead = file.read(buffer);
        if (bytesRead != ConstValues.PAGE_SIZE) {
            throw new IOException("Incomplete page read: " + bytesRead + " bytes");
        }
        if(!printing){
            Statistic.incrementReadBlocksCounter();
        }
    }

    public void writePage(int pageAddress, byte[] buffer) throws IOException{
        long offset = (long) pageAddress * ConstValues.PAGE_SIZE;
        file.seek(offset);
        file.write(buffer);
        Statistic.incrementWriteBlocksCounter();
    }

    public int setPageAddress() throws IOException{
        return (int)(file.length() / ConstValues.PAGE_SIZE);
    }

    public void delete() throws IOException {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
        } catch (IOException e) {
            throw new IOException("błąd podczas czyszczenia pliku: " + fileName, e);
        }
    }
}
