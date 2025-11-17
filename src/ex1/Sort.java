package ex1;

import java.io.File;
import java.io.IOException;
import java.util.List;
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

    public static void mergeRuns(List<File> runsList){

    }

}
