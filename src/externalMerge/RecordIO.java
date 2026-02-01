package externalMerge;

import java.io.IOException;
import java.util.List;

public interface RecordIO {
    Record readRecord() throws IOException;

    void writeRun(List<Record> records, int runIndex) throws IOException;

    void writeRecord(Record record) throws IOException;

    void close() throws IOException;

    void openToRead() throws IOException;

    void openToWrite() throws IOException;

    boolean deleteFile();
}
