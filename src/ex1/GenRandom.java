package ex1;

import java.io.FileWriter;
import java.io.IOException;
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
                record1 = Math.round(record1 * 100.0) / 100.0;
                record2 = Math.round(record2 * 100.0) / 100.0;
                myFile.write(record1 + ";" + record2 + "\n");
            }
            myFile.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace(); // Print error details
        }
    }
}
