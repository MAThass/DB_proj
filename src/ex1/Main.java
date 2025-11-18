import ex1.*;
import ex1.Record;
import java.io.IOException;
import java.nio.file.*;


void main() throws IOException {
    int N = ConstValues.numberOfRecord;
    int b = ConstValues.blockingFactor;
    int n = ConstValues.numberOfBuffers;

    GenRandom.createFile(ConstValues.numberOfRecord);
    RecordIO IO = new HandleFile("gen.csv");
    //List<Record> recordList = new ArrayList<>();
    try {
        IO.openToRead();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }


    int runIndex = 0;
    List<Record> recordsInByffer = new ArrayList<>(n*b);

    Record record;
    //#################
    while ((record = IO.readRecord()) != null) {
        recordsInByffer.add(record);

        if(recordsInByffer.size() == n*b){
            Sort.heapSort(recordsInByffer);
            IO.writeRun(recordsInByffer, runIndex);
            runIndex++;
            recordsInByffer.clear();
        }
    }

    if(!recordsInByffer.isEmpty()){
        Sort.heapSort(recordsInByffer);
        IO.writeRun(recordsInByffer, runIndex);
        runIndex++;
        recordsInByffer.clear();
    }
    //################# wczytanie danych i stworzenie posortowanych biegow

    File runsFolder = new File("runs");
    File[] runsFiles = runsFolder.listFiles();
    //List<File> runslist = Arrays.asList(runsFiles);
    List<RecordIO> runsList = new ArrayList<>(runsFiles.length);

    for (int i = 0; i < runsFiles.length; i++) {
        runsList.add(new HandleFile(runsFiles[i].getAbsolutePath()));
    }

    while(runsFiles.length > 1){
        for (int i = 0; i < runsList.size(); i += (n - 1)) {
            // the number of runes may not be a multiple of the range
            int lastRun = Math.min(i + (n - 1), runsList.size());
            List<RecordIO> group = runsList.subList(i, lastRun);
            Sort.mergeRuns(group, runIndex);
            runIndex++;
            //zapisanie run na dysku ???
        }
        runsFiles = runsFolder.listFiles();
        runsList.clear();
        for (int i = 0; i < runsFiles.length; i++) {
            runsList.add(new HandleFile(runsFiles[i].getAbsolutePath()));
        }
    }

    IO.close();

    System.out.println("read block "+Statistic.readBlocksCounter);
    System.out.println("write block "+Statistic.writeBlocksCounter);
     N = ConstValues.numberOfRecord;
     b = ConstValues.blockingFactor;
     n = ConstValues.numberOfBuffers;

    double B = N / b;

    double expected = 2 * B * (Math.log(B) / Math.log(n));  // log_n(B)

    System.out.println("Expected IO = " + expected);
}
