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
            // if child is bigger than parent
            if (recordList.get(i).getPotential_energy() > recordList.get((i - 1) / 2).getPotential_energy())
            {
                int j = i;
                // swap child and parent until
                // parent is smaller
                while (recordList.get(j).getPotential_energy() > recordList.get((j - 1) / 2).getPotential_energy())
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
            // swap value of first indexed
            // with last indexed
            swap(recordList, 0, i);

            // maintaining heap property
            // after each swapping
            int j = 0, index;

            do
            {
                index = (2 * j + 1);

                // if left child is smaller than
                // right child point index variable
                // to right child
                if (index < (i - 1) && recordList.get(index).getPotential_energy() < recordList.get(index + 1).getPotential_energy())
                    index++;

                // if parent is smaller than child
                // then swapping parent with child
                // having higher value
                if (index < i && recordList.get(j).getPotential_energy() < recordList.get(index).getPotential_energy())
                    swap(recordList, j, index);

                j = index;

            } while (index < i);
        }
    }

    public static void buildMinHeapForRuns(){

    }

    public static void mergeRuns(List<RecordIO> runsList, int runIndex) throws IOException {
        RecordIO mergedRun = new HandleFile("runs/run" + runIndex + ".csv");
        try {
            mergedRun.openToWrite();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        for (RecordIO file : runsList) {
            file.openToRead();
        }

        PriorityQueue<Map.Entry<Record, Integer>> minHeap = new PriorityQueue<>(
                new Comparator<Map.Entry<Record, Integer>>() {
                    public int compare(Map.Entry<Record, Integer> e1, Map.Entry<Record, Integer> e2) {
                        // Porównujemy rekordy według klucza (zakładamy, że Record implements Comparable)
                        return e1.getKey().compareTo(e2.getKey());
                    }
                }
        );

        // Wczytaj pierwszy rekord z każdego pliku wejściowego i dodaj go do kopca
        for (int i = 0; i < runsList.size(); i++) {
            Record rec = runsList.get(i).readRecord();
            if (rec != null) {
                // Dodajemy parę (rekord, indeks pliku) do kopca
                minHeap.add(new AbstractMap.SimpleEntry<>(rec, i));
            }
        }

        while (!minHeap.isEmpty()) {
            // Pobranie pary (rekord, źródłowy indeks pliku) o najmniejszym rekordzie
            Map.Entry<Record, Integer> entry = minHeap.poll();
            Record minRec = entry.getKey();
            int sourceIdx = entry.getValue();

            // Zapisz najmniejszy rekord do pliku wyjściowego
            mergedRun.writeRecord(minRec);

            // Wczytaj kolejny rekord z tego samego pliku źródłowego
            Record nextRec = runsList.get(sourceIdx).readRecord();
            if (nextRec != null) {
                // Dodajemy nowy rekord z tego pliku do kopca
                minHeap.add(new AbstractMap.SimpleEntry<>(nextRec, sourceIdx));
            }
        }

        // Zamknij wszystkie pliki wejściowe oraz plik wyjściowy
        for (RecordIO file : runsList) {
            file.close();
            file.deleteFile();
        }
        mergedRun.close();

    }

}
