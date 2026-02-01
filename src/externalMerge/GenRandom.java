package externalMerge;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

public class GenRandom {
    private static String fileName = "gen.csv";
    private static Random R = new Random();
    public static void createFile(int numberOfRecords) throws IOException {
        try {
            FileWriter myFile = new FileWriter(fileName); // Create File object
            for (int i = 0; i <numberOfRecords; i++) {
                double record1 = R.nextDouble(ConstValues.minRangeRecord,ConstValues.maxRangeRecord);
                double record2 = R.nextDouble(ConstValues.minRangeRecord,ConstValues.maxRangeRecord);
                String r1 = String.format(Locale.US, "%05.2f", record1);
                String r2 = String.format(Locale.US, "%05.2f", record2);
                myFile.write(r1 + ";" + r2 + "\n");
            }
            myFile.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace(); // Print error details
        }
    }
}
