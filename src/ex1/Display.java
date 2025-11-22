package ex1;

public class Display {
    public static void   MainMenu(){
        System.out.println("Jak generować rekordy");
        System.out.println("1. Losowo");
        System.out.println("2. Z pliku");
        System.out.println("3. Z klawiatury");
    }

    public static void   RandomMenu(){
        System.out.println("Podaj liczbe rekordów");
    }

    public static void   FielMenu(){
        System.out.println("Wybierz pliku");
    }

    public static void   KeyBoardMenu(){
        System.out.println("Wpisz rekord w formacie: masa wysokość");
        System.out.println("Aby zkończyć wpisz: 'q'");
    }

    public static void   IncorrectInputMessage(){
        System.out.println("Podaj poprawne dane");
    }

    public static void   printSumaryStage1(){
        System.out.println("RZECZYWISTE operacje fazy 1:");
        System.out.println("  Odczyty (Read): " + Statistic.readBlocksCounter);
        System.out.println("  Zapisy (Write): " + Statistic.writeBlocksCounter);
        System.out.println("  SUMA RZECZYWISTA FAZA 1: " + (Statistic.readBlocksCounter + Statistic.writeBlocksCounter));
        System.out.println("-------------------------------------------");
    }

    public static void printSumary(int numbersOfRecords){
        int N = numbersOfRecords;
        int b = ConstValues.blockingFactor;
        int n = ConstValues.numberOfBuffers;

        int totalBlocksInFile = N / b;
        int theoreticalStage1 = 2 * totalBlocksInFile;
        double numbersOfRuns = Math.ceil((double)N / (n * b));

        System.out.println("Parametry:");
        System.out.println("  N (rekordy): " + N);
        System.out.println("  b (blocking factor): " + b);
        System.out.println("  n (bufory): " + n);
        System.out.println("  Liczba bloków (N/b): " + totalBlocksInFile);
        System.out.println("  Liczba początkowych serii: " + (int)numbersOfRuns);
        System.out.println("-------------------------------------------");

        int k = 1;
        if(n > 2){
            k = n-1;
        }

        int mergePhases = (int) Math.ceil(Math.log(numbersOfRuns) / Math.log(k));
        int theoreticalStage2 = mergePhases * 2 * totalBlocksInFile;
        int theoreticalTotal = theoreticalStage1 + theoreticalStage2;

        System.out.println("RZECZYWISTE operacje:");
        System.out.println("  Odczyty         : " + Statistic.readBlocksCounter);
        System.out.println("  Zapisy          : " + Statistic.writeBlocksCounter);
        System.out.println("  SUMA RZECZYWISTA: " + (Statistic.readBlocksCounter + Statistic.writeBlocksCounter));
        System.out.println("  Cykle w fazie 2 : " + Statistic.cycleCounter);
        System.out.println("-------------------------------------------");

        System.out.println("TEORETYCZNE operacje:");
        System.out.println("  Faza 1 (2 * N/b)                  : " + theoreticalStage1);
        System.out.println("  Faza 2 ( ilość cykli(" + mergePhases + ") * 2 * N/b): " + theoreticalStage2);
        System.out.println("  SUMA TEORETYCZNA                  : " + theoreticalTotal);
        System.out.println("  Cykle w fazie 2                   : " + mergePhases);
        System.out.println("-------------------------------------------");

        if ((Statistic.readBlocksCounter + Statistic.writeBlocksCounter) == theoreticalTotal) {
            System.out.println("ZGODNOŚĆ Z TEORIĄ");
        } else {
            System.out.println("Rozbieżność z teorią");
        }
    }
}
