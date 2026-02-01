package BPlusTree;

public class Statistic {
    public  static int writeBlocksCounter = 0;
    public  static int readBlocksCounter = 0;

    public static void reset(){
        writeBlocksCounter = 0;
        readBlocksCounter = 0;
    }

    public static void incrementWriteBlocksCounter()
    {
       writeBlocksCounter++;
    }

    public static void incrementReadBlocksCounter()
    {
        readBlocksCounter++;
    }



    public static void printStats() {
        System.out.println("  Read operations: " + readBlocksCounter);
        System.out.println("  Write operations: " + writeBlocksCounter);
        System.out.println("  Total I/O operations: " + (readBlocksCounter + writeBlocksCounter));
    }
}
