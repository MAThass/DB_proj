package ex1;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.nio.charset.StandardCharsets;

public class HandleFile implements RecordIO {
    private String fileName;
    private int blockSize;
    private BufferedInputStream bInStream;
    private BufferedOutputStream bOutStream;
    private byte[] readBlockBuffer;
    private int readPosition = 0;
    private int readValidBytes = 0;
    private boolean EOF = false;
    private byte[] writeBlockBuffer;
    private int writePosition = 0;



    public HandleFile(String fileName) {
        this.fileName = fileName;
        this.blockSize = ConstValues.blockSize;
    }

    public HandleFile(String fileName, int blockSize) {
        this.fileName = fileName;
        this.blockSize = blockSize;
    }

    //### READ ####

    @Override
    public void openToRead() throws IOException {
        bInStream = new BufferedInputStream(new FileInputStream(fileName));
        readBlockBuffer = new byte[blockSize];
        loadNextBlock();
    }

    private void loadNextBlock() throws IOException {
        if(EOF)
            return;
        readValidBytes = bInStream.read(readBlockBuffer);

        if(readValidBytes == -1){
            EOF = true;
            readValidBytes = 0;
        } else {
            Statistic.incrementReadBlocksCounter();
        }

        readPosition = 0;
    }

    @Override
    public Record readRecord() throws IOException{
        if(EOF && readValidBytes <= readPosition)
            return null;

        StringBuilder sb = new StringBuilder();

        while(true){
            if(readPosition >= readValidBytes){
                loadNextBlock();
                if(EOF && readValidBytes == 0){
                    if(sb.length() > 0){
                        return new Record(sb.toString());
                    }
                    return null;
                }
            }

            char oneChar = (char) readBlockBuffer[readPosition];
            readPosition++;

            if(oneChar == '\n'){
                return new Record(sb.toString());
            }

            if (oneChar != '\r') {
                sb.append(oneChar);
            }
        }
    }

    //### WRITE ###

    @Override
    public void openToWrite() throws IOException {
        bOutStream = new BufferedOutputStream(new FileOutputStream(fileName));
        writeBlockBuffer = new byte[blockSize];
        writePosition = 0;
    }

    @Override
    public void writeRecord(Record record) throws IOException {
        String line = record.toString() + "\n";
        byte[] data = line.getBytes(StandardCharsets.UTF_8);

        int offset = 0;

        while(offset < data.length){
            int leftSpace = blockSize - writePosition;
            int toCopy = Math.min(leftSpace, data.length - offset);

            System.arraycopy(data, offset, writeBlockBuffer, writePosition, toCopy);

            offset += toCopy;
            writePosition += toCopy;

            if(writePosition == blockSize){
                executeWriteBlock();
            }
        }
    }

    private void executeWriteBlock() throws IOException {
        if(writePosition > 0){
            bOutStream.write(writeBlockBuffer, 0, writePosition);
            Statistic.incrementWriteBlocksCounter();
            writePosition = 0;
        }
    }


    @Override
    public void writeRun(List<Record> records, int runIndex) throws IOException {
        String runName = "runs/run" +  runIndex + ".csv";
        HandleFile runFile =  new HandleFile(runName,this.blockSize);
        runFile.openToWrite();
        for(Record record : records) {
            runFile.writeRecord(record);
        }
        runFile.close();
    }



    public boolean deleteFile() {
        try {
            this.close();
        } catch (IOException e) {
            System.err.println("Błąd podczas zamykania strumieni pliku: " + fileName);
            e.printStackTrace();
        }
        File f = new File(fileName);
        if (!f.exists()) {
            System.out.println("Plik nie istnieje" + fileName);
            return false;
        }
        boolean deleted = f.delete();
        if (!deleted) {
            System.err.println("Nie udało się usunąć pliku: " + fileName);
        }
        return deleted;
    }


    @Override
    public void close() throws IOException {
        if (bInStream != null) bInStream.close();
        if (bOutStream != null){
            executeWriteBlock();
            bOutStream.close();
        }
    }

}