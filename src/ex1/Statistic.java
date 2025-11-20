package ex1;

public class Statistic {
    public  static int writeBlocksCounter = 0; // licznik zapisanych bloków
    public  static int readBlocksCounter = 0;  // licznik odczytanych bloków
    public  static int cycleCounter = 0;

    public static void reset(){
        writeBlocksCounter = 0;
        readBlocksCounter = 0;
        cycleCounter = 0;
    }

    public static void incrementWriteBlocksCounter()
    {
       writeBlocksCounter++;
    }

    public static void incrementReadBlocksCounter()
    {
        readBlocksCounter++;
    }

    public static void incrementCycleCounter()
    {
        cycleCounter++;
    }
}
