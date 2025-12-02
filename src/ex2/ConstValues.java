package ex2;

public class ConstValues {
    public static final double g_const = 9.81;
    public static final int numberOfRecord = 100000;
    public static final int blockingFactor = 100;
    public static final int numberOfBuffers = 100;
    public static final int blockSize = blockingFactor*12;
    public static final int minRangeRecord = 1;
    public static final int maxRangeRecord = 99;
    public static final boolean printFile = true;
    public static boolean printRuns = true;
    public static final int numberOfLinesToPrint = 500;


    public static final int PAGE_SIZE = 512;
    // HEDDDER 13b, padding to 16B
    // for internal maxKeys * 4 + ( maxKeys + 1 ) * 4 = maxKeys * 8 + 4 [B] <= pagesizen - 16
    // for leaf 4 + 4 + maxRecords * 4 + 2 * 8  * maxRecords = 24 * maxRecords + 8 [B] <= pagesize - 16
    private static final int SIZE_INT = Integer.SIZE;
    private static final int SIZE_DOUBLE = Double.SIZE;
    private static final int SIZE_RECORD = 2 * SIZE_DOUBLE + SIZE_INT;
    private static final int SIZE_HEADER = 4 * SIZE_INT;
    public static final int MAX_INTERNAL_KEYS = (PAGE_SIZE - SIZE_HEADER - SIZE_INT) / (2 * SIZE_INT);
    public static final int MAX_LEAF_KEYS = (PAGE_SIZE - SIZE_HEADER - 2 * SIZE_INT) / SIZE_RECORD;

    public static final int TREE_DEGREE = Math.max(MAX_INTERNAL_KEYS, MAX_LEAF_KEYS) / 2;
}
