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
    private String inputBlock = "";
    private byte[] writeBlockBuffer;
    private int writePos = 0;
    private Boolean EOF = false;



    public HandleFile(String fileName) {
        this.fileName = fileName;
        this.blockSize = ConstValues.blockSize;
    }

    public HandleFile(String fileName, int blockSize) {
        this.fileName = fileName;
        this.blockSize = blockSize;
        int writePos = 0;
    }

    public void openToRead() throws IOException {
        bInStream = new BufferedInputStream(new FileInputStream(fileName));
    }

    public void openToWrite() throws IOException {
        bOutStream = new BufferedOutputStream(new FileOutputStream(fileName));
        writeBlockBuffer = new byte[blockSize];
    }

    @Override
    public Record readRecord() throws IOException {
        if (EOF) return null;
        StringBuilder lineBuilder = new StringBuilder(inputBlock);

        byte[] buffer = new byte[blockSize]; //create buffer to read chunks of input
        int bytesRead;
        while (true) {
            int newlinePos = lineBuilder.indexOf("\n");  // find position of new line, last element od record
            if (newlinePos >= 0) {  // record exist before new line
                String line = lineBuilder.substring(0, newlinePos).trim(); // extract record and remove white spaces
                inputBlock = lineBuilder.substring(newlinePos + 1); // save rest of input, without first record
                if (!line.isEmpty())  // if in line is record return it, else read next chunk
                    return new Record(line);
            }

            bytesRead = bInStream.read(buffer); // read chunk of input
            if(bytesRead != -1)
            {

                Statistic.incrementReadBlocksCounter();
                lineBuilder.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8)); // add readed chunk to lineBuilder
            }else { // if not read data
                EOF = true;
                if (lineBuilder.length() == 0) // if there are not and record in memory
                    return null;
                String last = lineBuilder.toString().trim();
                if (!last.isEmpty())
                    return new Record(last);
                return null;
            }
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

    private void writeBlock() throws IOException {
        if (writePos == 0) return;

        bOutStream.write(writeBlockBuffer, 0, writePos);
        Statistic.incrementWriteBlocksCounter();
        writePos = 0;
    }

    @Override
    public void writeRecord(Record record) throws IOException {
        String line = record.toString() + "\n";
        byte[] data = line.getBytes(StandardCharsets.UTF_8);

        if (writePos + data.length > blockSize) {
            writeBlock();
        }
        // z data kopiujemy od elementu 0 do writeBlockBuffer od pozucji writePos ata.lenght elementow
        System.arraycopy(data, 0, writeBlockBuffer, writePos, data.length);
        writePos += data.length;
    }

    @Override
    public void close() throws IOException {
        if (bInStream != null) bInStream.close();
        if (bOutStream != null){
            writeBlock();
            bOutStream.close();
        }
    }

}