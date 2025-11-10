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
    private Boolean EOF = false;

    public HandleFile(String fileName) {
        this.fileName = fileName;
        this.blockSize = ConstValues.blockSize;
    }

    public HandleFile(String fileName, int blockSize) {
        this.fileName = fileName;
        this.blockSize = blockSize;
    }

    public void openToRead() throws IOException {
        bInStream = new BufferedInputStream(new FileInputStream(fileName));
    }

    public void openToWrite() throws IOException {
        bOutStream = new BufferedOutputStream(new FileOutputStream(fileName));
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
            if (bytesRead == -1) { // if not read data
                EOF = true;
                if (lineBuilder.length() == 0) // if there are not and record in memory
                    return null;
                String last = lineBuilder.toString().trim();
                if (!last.isEmpty())
                    return new Record(last);
                return null;
            }

            lineBuilder.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8)); // add readed chunk to lineBuilder
        }
    }

    @Override
    public void saveRun(List<Record> records, int runIndex) throws IOException {
        String runName = "runs/run" +  runIndex + ".csv";
        HandleFile runFile =  new HandleFile(runName,this.blockSize);
        runFile.openToWrite();
        for(Record record : records) {
            runFile.writeRecord(record);
        }
        runFile.close();
    }

    @Override
    public void writeRecord(Record record) throws IOException {
        String line = record.toString();
        bOutStream.write(line.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void close() throws IOException {
        if (bInStream != null) bInStream.close();
        if (bOutStream != null) bOutStream.close();
    }

}