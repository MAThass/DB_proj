import ex1.*;
import ex1.Record;


void main() throws IOException {
    int N = ConstValues.numberOfRecord;
    int b = ConstValues.blockingFactor;
    int n = ConstValues.numberOfBuffers;


    RecordIO IO = new HandleFile("data.csv", 60);
    //List<Record> recordList = new ArrayList<>();
    try {
        IO.openToRead();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }


    int runIndex = 0;
    List<Record> recordsInByffer = new ArrayList<>(n*b);

    Record record;
    while ((record = IO.readRecord()) != null) {
        recordsInByffer.add(record);

        if(recordsInByffer.size() == n*b){
            Sort.heapSort(recordsInByffer);
            IO.saveRun(recordsInByffer, runIndex);
            runIndex++;
            recordsInByffer.clear();
        }
    }

    if(!recordsInByffer.isEmpty()){
        Sort.heapSort(recordsInByffer);
        IO.saveRun(recordsInByffer, runIndex);
        recordsInByffer.clear();
    }

    IO.close();
}
