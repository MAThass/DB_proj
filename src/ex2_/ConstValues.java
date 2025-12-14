package ex2_;

public class ConstValues {
    public static final int PAGE_SIZE = 1024;
    public static final int d =2;
    public static final double alpha = 75; // %
    public static final int FILING_LEVEL = (int) (d*2*(alpha/100));

    public static final int MAX_LEAF_KEYS = 4;
}
//for N=1000 d=2 alpha=75