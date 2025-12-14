package ex2_;

public class Statistic {
    public  static int writeBlocksCounter = 0;
    public  static int readBlocksCounter = 0;
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
