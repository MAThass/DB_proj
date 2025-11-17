package ex1;

public class Statistic {
    public  static int writeBlocksCounter = 0; // licznik zapisanych bloków
    public  static int readBlocksCounter = 0;  // licznik odczytanych bloków

    public static void incrementWriteBlocksCounter()
    {
       writeBlocksCounter++;
    }

    public static void incrementReadBlocksCounter()
    {
        readBlocksCounter++;
    }
}
