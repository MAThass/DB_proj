package ex1;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.util.Collections.swap;

public class Sort {
    static void heapify(List<Record> recordList){
        int length = recordList.size();
        for (int i = 1; i < length; i++)
        {
            if (recordList.get(i).getPotentialEnergy() > recordList.get((i - 1) / 2).getPotentialEnergy())
            {
                int j = i;
                while (recordList.get(j).getPotentialEnergy() > recordList.get((j - 1) / 2).getPotentialEnergy())
                {
                    swap(recordList, j, (j - 1) / 2);
                    j = (j - 1) / 2;
                }
            }
        }
    }

    public static void heapSort(List<Record> recordList){
        int length = recordList.size();

        heapify(recordList);
        for (int i = length - 1; i > 0; i--)
        {
            swap(recordList, 0, i);
            int j = 0, index;

            do
            {
                index = (2 * j + 1);
                if (index < (i - 1) && recordList.get(index).getPotentialEnergy() < recordList.get(index + 1).getPotentialEnergy())
                    index++;
                if (index < i && recordList.get(j).getPotentialEnergy() < recordList.get(index).getPotentialEnergy())
                    swap(recordList, j, index);
                j = index;
            } while (index < i);
        }
    }

    public static void mergeRuns(List<RecordIO> group, int runIndex) throws IOException {
        RecordIO output = new HandleFile("runs/run" + runIndex + ".csv");
        try {
            output.openToWrite();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        for (RecordIO file : group) {
            file.openToRead();
        }

        PriorityQueue<QueueRcord> minHeap = new PriorityQueue<>(group.size());

        for (int i = 0; i < group.size(); i++) {
            Record initialRecord = group.get(i).readRecord();
            if (initialRecord != null) {
                minHeap.add(new QueueRcord(initialRecord,i));
            }
        }

        while (!minHeap.isEmpty()) {
            QueueRcord minEntry = minHeap.poll();
            output.writeRecord(minEntry.record);
            Record nextRecord = group.get(minEntry.index).readRecord();
            if (nextRecord != null) {
                minHeap.add(new QueueRcord(nextRecord,minEntry.index));
            }
        }

        for (RecordIO file : group) {
            file.close();
            file.deleteFile();
        }
        output.close();

    }

}
