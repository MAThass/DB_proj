import ex1.*;
import ex1.Record;
import java.io.IOException;
import java.nio.file.*;


void main() throws IOException {
    int N = ConstValues.numberOfRecord;
    int b = ConstValues.blockingFactor;
    int n = ConstValues.numberOfBuffers;

    GenRandom.createFile(ConstValues.numberOfRecord);
    RecordIO IO = new HandleFile("gen.csv", 1024);
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
        recordsInByffer.clear();
    }
    //################# wczytanie danych i stworzenie posortowanych biegow

    File runsFolder = new File("runs");
    File[] runsFiles = runsFolder.listFiles();
    long runsNumber = runsFiles.length;
    List<File> runslist = Arrays.asList(runsFiles);
    while(runsNumber > 1){
        for (int i = 0; i < runslist.size(); i += (n - 1)) {
            // the number of runes may not be a multiple of the range
            int lastRun = Math.min(i + (n - 1), runslist.size());
            List<File> group = runslist.subList(i, lastRun);
            Sort.mergeRuns(group);
            //zapisanie run na dysku ???
        }


        runsNumber =runsFiles.length;
    }

    IO.close();



}
