package ex1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MergingWithLargeBuffers {
    public static void Merge(String FileName) throws IOException {
        clearRunsFolder();

        if(ConstValues.printFile){
            Display.printNotSortedFile(FileName);
        }

        int numberOfRecord = 0;
        int b = ConstValues.blockingFactor;
        int n = ConstValues.numberOfBuffers;

        RecordIO IO = new HandleFile(FileName);
        try {
            IO.openToRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int runIndex = 0;
        List<Record> recordsInByffer = new ArrayList<>(n*b);
        Record record;
        //STAGE 1
        while ((record = IO.readRecord()) != null) {
            numberOfRecord++;
            recordsInByffer.add(record);

            if(recordsInByffer.size() == n*b){
                //Sort.heapSort(recordsInByffer);
                Collections.sort(recordsInByffer);
                IO.writeRun(recordsInByffer, runIndex);
                runIndex++;
                recordsInByffer.clear();
            }
        }
        if(!recordsInByffer.isEmpty()){
            //Sort.heapSort(recordsInByffer);
            Collections.sort(recordsInByffer);
            IO.writeRun(recordsInByffer, runIndex);
            runIndex++;
            recordsInByffer.clear();
        }

        File runsFolder = new File("runs");
        File[] runsFiles = runsFolder.listFiles();
        List<RecordIO> runsList = new ArrayList<>(runsFiles.length);

        //STAGE 2
        for (File file : runsFiles) {
            runsList.add(new HandleFile(file.getAbsolutePath()));
        }

        while(runsFiles.length > 1){
            if(ConstValues.printRuns){
                Display.printRuns();
            }
            Statistic.incrementCycleCounter();
            for (int i = 0; i < runsList.size(); i += (n - 1)) {
                int lastRun = Math.min(i + (n - 1), runsList.size());
                List<RecordIO> group = runsList.subList(i, lastRun);
                Sort.mergeRuns(group, runIndex);
                runIndex++;
            }
            runsList.clear();
            runsFiles = runsFolder.listFiles();
            for (File runsFile : runsFiles) {
                runsList.add(new HandleFile(runsFile.getAbsolutePath()));
            }

        }

        IO.close();
        if(ConstValues.printFile){
            Display.printSortedFile();
        }
        Display.printSumary(numberOfRecord);
    }

    private static void clearRunsFolder()
    {
        File runsFolder = new File("runs");
        if (runsFolder.exists() && runsFolder.isDirectory()) {
            File[] files = runsFolder.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }
        Statistic.reset();
    }
}
